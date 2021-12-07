#! /usr/bin/env python

import socket
import sys
import traceback
import threading
import select

from message import Message
from messagetype import MessageType

SOCKET_LIST = []
TO_BE_SENT = []
SENT_BY = {}

PORT = 5537

class KeySet:
    def __init__(self, pub = None, sym = None):
        self.pub = pub
        self.sym = sym

class Keys:

    pvt_key = '' # TODO generate
    pub_key = '' # TODO generate
    __CLIENTS_SETS = {} # a dict of {ip, KeySet}

    @classmethod
    def getForIp(self, ip: str) -> KeySet:
        '''Get a client's KeySet if it exists, or create an empty one if it doesn't'''
        set = self.__CLIENTS_SETS.get(ip)
        if set == None:
            self.__CLIENTS_SETS[ip] = KeySet()
            set = self.__CLIENTS_SETS.get(ip)
        return set

    @classmethod
    def setForIp(self, ip: str, set: KeySet) -> KeySet:
        '''Adds or replaces a client's KeySet, and returns it back'''
        self.__CLIENTS_SETS[ip] = set
        return set


def do_handshake(ip: str, pub_key: str): # TODO
    pub_key_msg = Message(MessageType.PUB_KEY_EXCHANGE, Keys.pub_key)
    # send pub_key_msg.serialize()

    sym_key = '' # TODO generate
    Keys.setForIp(ip, KeySet(pub_key, sym_key))

    encrypted_sym_key = '' # TODO encrypt it with the server's pvt_key & the client's pub_key (which order??)
    sym_key_msg = Message(MessageType.SYM_KEY_EXCHANGE, encrypted_sym_key)
    # send sym_key_msg.serialize()

    return

def route_msg(from_ip: str, content: str):
    content = '' # TODO decrypt it with from_ip's sym_key
    # TODO loop through connected clients
        # warp code bellow in try except, with do_handshake if fails

        # if ip !== from_ip:
        # msg = Message(MessageType.NORMAL, content)
        # send msg.serialize()
    return

def proccess_income_msg(from_ip: str, msg_bytes: bytes):
    msg = Message.deserialize(msg_bytes)
    match msg.type:
        case MessageType.PUB_KEY_EXCHANGE:
            do_handshake(from_ip, msg.content)
            return
        case MessageType.NORMAL:
            route_msg(from_ip, '') # TODO decrypt msg.content with from_ip sym key
            return


class Server(threading.Thread):

    def init(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, 1)
        self.sock.bind(('', PORT))
        self.sock.listen(2)
        SOCKET_LIST.append(self.sock)
        print("Server started on port " + str(PORT))

    def run(self):
        while 1:
            read, write, err = select.select(SOCKET_LIST, [], [], 0)
            for sock in read:
                if sock == self.sock:
                    sockfd, addr = self.sock.accept()
                    print(str(addr))

                    SOCKET_LIST.append(sockfd)
                    print(SOCKET_LIST[len(SOCKET_LIST) - 1])

                else:
                    try:
                        s = sock.recv(1024)
                        if s == '':
                            print(str(sock.getpeername()))
                            continue
                        else:
                            TO_BE_SENT.append(s)
                            SENT_BY[s] = (str(sock.getpeername()))
                    except:
                        print(str(sock.getpeername()))


class handle_connections(threading.Thread):
    def run(self):
        while 1:
            read, write, err = select.select([], SOCKET_LIST, [], 0)
            for items in TO_BE_SENT:
                for s in write:
                    try:
                        if (str(s.getpeername()) == SENT_BY[items]):
                            print("Ignoring %s" % (str(s.getpeername())))
                            continue
                        print("Sending to %s" % (str(s.getpeername())))
                        s.send(items)

                    except:
                        traceback.print_exc(file=sys.stdout)
                TO_BE_SENT.remove(items)
                del (SENT_BY[items])


if __name__ == '__main__':
    srv = Server()
    srv.init()
    srv.start()
    print(SOCKET_LIST)
    handle = handle_connections()
    handle.start()