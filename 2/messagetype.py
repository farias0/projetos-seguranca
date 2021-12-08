from enum import Enum, unique

@unique
class MessageType(Enum):
    ASK_FOR_PUB_KEY = 0 # used by server in case of error
    PUB_KEY_EXCHANGE = 1 # used by the client & server
    SYM_KEY_EXCHANGE = 2  # used by the server only
    NORMAL = 3

    def serialize(self) -> bytes:
        '''Transforms a MessageType enum into a 1-byte byte string'''

        return self.value.to_bytes(self.BYTE_LEN, byteorder=self.BYTE_ORDER)

    @classmethod
    def deserialize(self, byte_value: str) -> 'MessageType':
        '''Recreates the MessageType from a byte string created with serialize()'''

        value = int.from_bytes(byte_value, byteorder=self.BYTE_ORDER)
        for type in MessageType:
            if type.value == value:
                return type
        raise ValueError

# workaround for adding static attributes to enums
MessageType.BYTE_LEN = 1
MessageType.BYTE_ORDER = 'big'