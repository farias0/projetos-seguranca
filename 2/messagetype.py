from enum import Enum, unique

@unique
class MessageType(Enum):
    PUB_KEY_EXCHANGE = 1 # distinguish between from client vs from server?
    SYM_KEY_EXCHANGE = 2
    NORMAL = 3

    def serialize(self) -> str:
        '''Transforms a MessageType enum in a 1-byte string to be appended to messages'''
        return self.value.to_bytes(1, byteorder='big')

    @classmethod
    def deserialize(self, byte_value: str) -> 'MessageType':
        '''Returns the MessageType from a serialized byte string'''
        value = int.from_bytes(byte_value, byteorder='big')
        for type in MessageType:
            if type.value == value:
                return type
        raise ValueError