#! /usr/bin/env python

import socket
import sys
import time
import threading
import select
import traceback

# >> libs usadas
import os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

from message import Message
from messagetype import MessageType
from rsa import RSA
from aes import AES


class Server(threading.Thread):
    __CLIENT = None

    def initialise(self, receive, client):
        self.receive = receive
        self.__CLIENT = client

    def run(self):
        lis = []
        lis.append(self.receive)
        while 1:
            read, write, err = select.select(lis, [], [])
            for item in read:
                try:
                    s = item.recv(1024)
                    if s != '':
                        chunk = s
                        
                        # >>

                        msg = Message.deserialize(chunk)
                        try:
                            keys = self.__CLIENT.Keys
                            match msg.type:
                                case MessageType.ASK_FOR_PUB_KEY:
                                    print('WARN: Server asked for pub key')
                                    self.__CLIENT.initiate_handshake()
                                    continue
                                case MessageType.PUB_KEY_EXCHANGE:
                                    keys.srv_pub_key = RSA.deserialize_pub_key(msg.content)
                                    continue
                                case MessageType.SYM_KEY_EXCHANGE:
                                    keys.sym_key = RSA.decrypt(RSA.verify_and_extract(msg.content, keys.srv_pub_key), keys.asym.pvt)
                                    continue
                                case MessageType.NORMAL:
                                    content = AES.decrypt(msg.content, keys.sym_key)
                                    print(content.decode() + '\n>>', end='')
                                    continue
                        except Exception as e:
                            # self.__CLIENT.initiate_handshake()
                            print('exception!')
                            print(e)
                            return

                        # <<

                except:
                    traceback.print_exc(file=sys.stdout)
                    break


class Client(threading.Thread):
    __HOST = None
    __PORT = None

    # >>

    class Keys:
        asym = RSA.gen_keys()
        sym_key = None
        srv_pub_key = None

    def initiate_handshake(self) -> None:
        '''To be ran at startup; sends the pub key to the server, initiating the handshake'''
        print('Initiating handshake with the server')
        msg = Message(MessageType.PUB_KEY_EXCHANGE, self.Keys.asym.serialized_pub())
        self.client(self.__HOST, self.__PORT, msg.serialize())
        return

    def send_msg(self, content: str) -> None:
        '''Encrypts a regular message using AES, hashes it and sends it to the server'''
        if self.Keys.sym_key == None:
            print("reconnecting to server...")
            self.initiate_handshake()
            return

        encrypted_content = AES.encrypt(content.encode(), self.Keys.sym_key)
        msg = Message(MessageType.NORMAL, encrypted_content)
        self.client(self.__HOST, self.__PORT, msg.serialize())
        return

    # <<

    def connect(self, host, port):
        self.sock.connect((host, port))

    def client(self, host, port, msg):
        sent = self.sock.send(msg)
        # print "Sent\n"

    def run(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        try:
            self.__HOST = input("Enter the server IP \n>>")
            self.__PORT = int(input("Enter the server Destination Port\n>>"))
        except EOFError:
            print("Error")
            return 1

        print("Connecting\n")
        s = ''
        self.connect(self.__HOST, self.__PORT)
        print("Connected\n")
        user_name = input("Enter the User Name to be Used\n>>")
        receive = self.sock
        time.sleep(1)
        srv = Server()
        srv.initialise(receive, self)
        srv.daemon = True
        print("Starting service")
        srv.start()
        self.initiate_handshake()
        while 1:
            # print "Waiting for message\n"
            msg = input('>>')
            if msg == 'exit':
                break
            if msg == '':
                continue
            # print "Sending\n"
            msg = user_name + ': ' + msg
            self.send_msg(msg)
        return (1)


if __name__ == '__main__':
    print("Starting client")
    cli = Client()
    cli.start()