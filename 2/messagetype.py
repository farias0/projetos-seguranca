from enum import Enum, unique

@unique
class MessageType(Enum):
    PUB_KEY_EXCHANGE = 1 # distinguish between from client vs from server?
    SYM_KEY_EXCHANGE = 2
    NORMAL = 3

    ## given a MessageType, returns its value
    @classmethod
    def serialize(self, type):
        return type.value

    ## given a value, return its MessageType
    @classmethod
    def deserialize(self, value):
        for type in MessageType:
            if type.value == value:
                return type
        raise ValueError