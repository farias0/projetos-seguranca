from enum import Enum, unique

@unique
class MessageType(Enum):
    PUB_KEY_EXCHANGE = 1 # used by the client & server
    SYM_KEY_EXCHANGE = 2  # used by the server only
    NORMAL = 3

    def serialize(self) -> bytes:
        '''Transforms a MessageType enum in a 1-byte string to be appended to messages'''

        return self.value.to_bytes(self.BYTE_LEN, byteorder=self.BYTE_ORDER)

    @classmethod
    def deserialize(self, byte_value: str) -> 'MessageType':
        '''Returns the MessageType from a serialized byte string'''

        value = int.from_bytes(byte_value, byteorder=self.BYTE_ORDER)
        for type in MessageType:
            if type.value == value:
                return type
        raise ValueError

# workaround for adding static attributes to enums
MessageType.BYTE_LEN = 1
MessageType.BYTE_ORDER = 'big'