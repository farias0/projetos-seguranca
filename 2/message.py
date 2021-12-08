from cryptography.hazmat.primitives import hashes
from messagetype import MessageType

'''
    SERIALIZED MESSAGE STRUCTURE:
    MessageType:    MessageType.BYTE_LEN bytes
    hash:           self.HASH.digest_size bytes
    content:        n bytes
'''

class Message():

    __HASH = hashes.SHA256()

    def __init__(self, type: MessageType, content: bytes = ''.encode()):
        # maybe should add type verifications here?
        self.type = type
        self.content = content

    def serialize(self) -> bytes:
        '''Returns a byte string version of this message with hashing, to be sent to a different node'''
        # aparently RSA crypto offers its own hashing, but for simplicity we'll use this method for all serialization

        return self.type.serialize() + self.__hash(self.content) + self.content

    @classmethod
    def deserialize(self, serialized_msg: bytes) -> 'Message':
        '''Recreates and hash-verify the message from a byte string created with serialize()'''

        hash_offset = MessageType.BYTE_LEN
        content_offset = hash_offset + self.__HASH.digest_size

        type = serialized_msg[:hash_offset]
        hash = serialized_msg[hash_offset:content_offset]
        content = serialized_msg[content_offset:]

        if (hash != self.__hash(content)):
            raise RuntimeError('Hash verification failed')

        return Message(MessageType.deserialize(type), content)

    @classmethod
    def __hash(self, bytes: bytes) -> bytes:
        digest = hashes.Hash(self.__HASH)
        digest.update(bytes)
        return digest.finalize()

    @classmethod
    def __string_to_bytes(self, content: str) -> bytes:
        # for some reason simple enconding/decoding was breaking in some situations
        return content.encode(encoding='UTF-8')

    @classmethod
    def __bytes_to_string(self, bytes: bytes) -> str:
        return bytes.decode(encoding='UTF-8')
