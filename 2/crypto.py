import os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

class AsymKeys:
    def __init__(self, pub: bytes = None, pvt: bytes = None):
        self.pub = pub
        self.pvt = pvt

class Crypto:

    __AES_KEY_LEN = 32
    __AES_IV_LEN = 16
    __AES_BLOCK_SIZE = algorithms.AES.block_size

    @classmethod
    def gen_AES_key(cls) -> bytes:
        '''Generates a AES (symmetric) key'''
        return os.urandom(cls.__AES_KEY_LEN)

    @classmethod
    def gen_RSA_keys(cls) -> AsymKeys:
        
        return AsymKeys()

    @classmethod
    def encrypt_AES(cls, msg: bytes, key: bytes) -> bytes:
        '''Encrypts a message using a AES key'''
        iv = os.urandom(cls.__AES_IV_LEN)
        cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
        encryptor = cipher.encryptor()
        encrypted_msg = encryptor.update(cls.__fill_msg_length(msg, cls.__AES_BLOCK_SIZE)) + encryptor.finalize()
        return iv + encrypted_msg

    @classmethod
    def encrypt_RSA(cls, msg: bytes, key: bytes) -> bytes:
        encrypted_msg = ''.encode()
        return encrypted_msg

    @classmethod
    def decrypt_AES(cls, chunk: bytes, key: bytes) -> bytes:
        '''Decrypts a message using a AES key'''
        iv = chunk[:cls.__AES_IV_LEN]
        encrypted_msg = chunk[cls.__AES_IV_LEN:]
        cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
        decryptor = cipher.decryptor()
        msg = decryptor.update(encrypted_msg) + decryptor.finalize()
        return msg

    @classmethod
    def decrypt_RSA(cls, encrypted_msg: bytes, key: bytes) -> bytes:
        return

    @classmethod
    def __fill_msg_length(cls, msg: bytes, total_len: int) -> bytes:
        '''Fiils the message with null bytes until the length equals total_len'''
        length = len(msg)
        fill = total_len - (length % total_len)
        return msg + ("\x00".encode() * fill)

    @classmethod
    def __clear_msg_length(cls, msg: bytes) -> bytes:
        '''Removes null bytes from the end of the message, undoing __fill_msg_length()'''
        while True:
            if msg[-1] == b'\x00':
                msg = msg[:-1]
            else:
                break