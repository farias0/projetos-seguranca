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

# >> chave secreta
KEY = b'\xf2pz\x06)O\x13\x8e\x9c\xd9\x94\xd8\n\x98u\xbfB\xb8\xf4*\xe4\x04e\xe5\xf9l,\x87\xf3d\x88\x99'

# >> tamanho do vetor de inicialização, em bytes
IV_SIZE = 16

# >> o tamanho da mensagem passada tem que ser multiplo de 128 (AES). essa função "enche linguiça"
def fill_msg_length(msg):
    length = len(msg)
    fill = 128 - (length % 128)
    return msg + ("\x00".encode() * fill)

## >> implementação da encriptação; vetor de inicialização é passado no começo da mensagem
def encrypt(msg):
    iv = os.urandom(IV_SIZE)
    cipher = Cipher(algorithms.AES(KEY), modes.CBC(iv))
    encryptor = cipher.encryptor()
    encrypted_msg = encryptor.update(fill_msg_length(msg.encode())) + encryptor.finalize()
    return iv + encrypted_msg

# >> implementação da decriptação; pega o vetor de inicialização no começo da mensagem
def decrypt(chunk):                        
    iv = chunk[0:IV_SIZE]
    encrypted_msg = chunk[IV_SIZE:]
    cipher = Cipher(algorithms.AES(KEY), modes.CBC(iv))
    decryptor = cipher.decryptor()
    decrypted_msg = decryptor.update(encrypted_msg) + decryptor.finalize()
    return decrypted_msg

class Server(threading.Thread):
    def initialise(self, receive):
        self.receive = receive

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
                        print(decrypt(chunk).decode() + '\n>>', end='')

                except:
                    traceback.print_exc(file=sys.stdout)
                    break


class Client(threading.Thread):
    def connect(self, host, port):
        self.sock.connect((host, port))

    def client(self, host, port, msg):
        sent = self.sock.send(msg)
        # print "Sent\n"

    def run(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        try:
            host = input("Enter the server IP \n>>")
            port = int(input("Enter the server Destination Port\n>>"))
        except EOFError:
            print("Error")
            return 1

        print("Connecting\n")
        s = ''
        self.connect(host, port)
        print("Connected\n")
        user_name = input("Enter the User Name to be Used\n>>")
        receive = self.sock
        time.sleep(1)
        srv = Server()
        srv.initialise(receive)
        srv.daemon = True
        print("Starting service")
        srv.start()
        while 1:
            # print "Waiting for message\n"
            msg = input('>>')
            if msg == 'exit':
                break
            if msg == '':
                continue
            # print "Sending\n"
            msg = user_name + ': ' + msg
            data = encrypt(msg)
            self.client(host, port, data)
        return (1)


if __name__ == '__main__':
    print("Starting client")
    cli = Client()
    cli.start()