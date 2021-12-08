import os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

class AES:

    __KEY_LEN = 32
    __IV_LEN = 16
    __BLOCK_SIZE = algorithms.AES.block_size

    @classmethod
    def gen_key(cls) -> bytes:
        '''Generates an AES (symmetric) key'''
        return os.urandom(cls.__KEY_LEN)

    @classmethod
    def encrypt(cls, msg: bytes, key: bytes) -> bytes:
        '''Encrypts a message using a AES key'''
        iv = os.urandom(cls.__IV_LEN)
        cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
        encryptor = cipher.encryptor()
        encrypted_msg = encryptor.update(cls.__fill_msg_length(msg, cls.__BLOCK_SIZE)) + encryptor.finalize()
        return iv + encrypted_msg

    @classmethod
    def decrypt(cls, chunk: bytes, key: bytes) -> bytes:
        '''Decrypts a message using an AES key'''
        iv = chunk[:cls.__IV_LEN]
        encrypted_msg = chunk[cls.__IV_LEN:]
        cipher = Cipher(algorithms.AES(key), modes.CBC(iv))
        decryptor = cipher.decryptor()
        msg = decryptor.update(encrypted_msg) + decryptor.finalize()
        return msg

    @classmethod
    def __fill_msg_length(cls, msg: bytes, total_len: int) -> bytes:
        '''Fiils the message with null bytes until the length equals total_len'''
        length = len(msg)
        fill = total_len - (length % total_len)
        return msg + (b"\x00" * fill)

    @classmethod
    def __clear_msg_length(cls, msg: bytes) -> bytes:
        '''Removes null bytes from the end of the message, undoing __fill_msg_length()'''
        while True:
            if msg[-1] == b'\x00':
                msg = msg[:-1]
            else:
                break