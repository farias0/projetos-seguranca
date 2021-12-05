from cryptography.hazmat.primitives import hashes
from messagetype import MessageType

'''
    SERIALIZED MESSAGE STRUCTURE:
    hash:           self.HASH.digest_size bytes
    MessageType:    MessageType.BYTE_LEN bytes
    content:        n bytes
'''

class Message():

    __HASH = hashes.SHA256()

    def __init__(self, type: MessageType, content: str):
        # maybe should add type verifications here?
        self.type = type
        self.content = content

    def serialize(self) -> bytes:
        '''Returns a byte string version of this message with hashing, to be sent to a different node'''

        byte_type = self.type.serialize()
        byte_content = self.__stringToBytes(self.content)
        to_be_hashed = byte_type + byte_content
        return  self.__hash(to_be_hashed) + byte_type + byte_content

    @classmethod
    def deserialize(self, serialized_msg: str) -> 'Message':
        '''Recreates the message from a byte string received from a node'''

        hash = serialized_msg[:self.__HASH.digest_size]
        everything_but_hash = serialized_msg[self.__HASH.digest_size:]

        if (hash != self.__hash(everything_but_hash)):
            raise RuntimeError('Hash verification failed')

        type = MessageType.deserialize(everything_but_hash[:MessageType.BYTE_LEN])
        content = self.__bytesToString(everything_but_hash[MessageType.BYTE_LEN:])

        return Message(type, content)

    @classmethod
    def __hash(self, bytes: bytes) -> bytes:
        digest = hashes.Hash(self.__HASH)
        digest.update(bytes)
        return digest.finalize()

    @classmethod
    def __stringToBytes(self, content: str) -> bytes:
        return content.encode(encoding='UTF-8')

    @classmethod
    def __bytesToString(self, bytes: bytes) -> str:
        return bytes.decode(encoding='UTF-8')
