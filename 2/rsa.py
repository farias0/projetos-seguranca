from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import rsa, padding

class _AsymKeys:
    # passing them separately like this is redundant, but adds to clarity IMO
    def __init__(self, pub: rsa.RSAPublicKey = None, pvt: rsa.RSAPrivateKey = None):
        self.pub = pub
        self.pvt = pvt

    def serialized_pub(self) -> bytes:
        '''Returns a serialized version of the public key, ready to be shared through messages'''
        return RSA.serialize_pub_key(self.pub)

class RSA:
    __KEY_LEN = 2048
    __SIGN_LEN = 256 # 294 or 256???

    @classmethod
    def gen_keys(cls) -> _AsymKeys:
        '''Generates a set of RSA (assymetric) keys'''
        pvt_key = rsa.generate_private_key(
            public_exponent=65537,
            key_size=cls.__KEY_LEN
        )
        return _AsymKeys(pvt_key.public_key(), pvt_key)

    @classmethod
    def serialize_pub_key(self, key: rsa.RSAPublicKey) -> bytes:
        '''Returns a serialized version of the public key, ready to be shared through messages'''
        return key.public_bytes(
            encoding=serialization.Encoding.DER,
            format=serialization.PublicFormat.SubjectPublicKeyInfo
        )

    @classmethod
    def deserialize_pub_key(self, bytes: bytes) -> rsa.RSAPublicKey:
        '''Builds back a public key from bytes serialized with serialize_pub_key()'''
        return serialization.load_der_public_key(bytes)

    @classmethod
    def sign(cls, msg: bytes, private_key: rsa.RSAPrivateKey) -> bytes:
        '''Signs a message using a RSA private key, to guarantee its authenticity'''
        signature = private_key.sign(
            msg,
            padding.PSS(
                mgf=padding.MGF1(hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            hashes.SHA256()
        )
        return signature + msg

    @classmethod
    def verify_and_extract(cls, chunk: bytes, public_key: rsa.RSAPublicKey) -> bytes:
        '''Verify the authenticity of a message signed with sign(), and extracts and returns the original message'''
        signature = chunk[:cls.__SIGN_LEN]
        msg = chunk[cls.__SIGN_LEN:]
        public_key.verify(
            signature,
            msg,
            padding.PSS(
                mgf=padding.MGF1(hashes.SHA256()),
                salt_length=padding.PSS.MAX_LENGTH
            ),
            hashes.SHA256()
        )
        return msg

    @classmethod
    def encrypt(cls, msg: bytes, public_key: rsa.RSAPublicKey) -> bytes:
        '''Encripts a message using RSA and returns the ciphertext'''
        return public_key.encrypt(
                msg,
                padding.OAEP(
                    mgf=padding.MGF1(algorithm=hashes.SHA256()),
                    algorithm=hashes.SHA256(),
                    label=None
                )
            )

    @classmethod
    def decrypt(cls, ciphertext: bytes, private_key: rsa.RSAPrivateKey) -> bytes:
        '''Decripts a message encrypted using encrypt()'''
        return private_key.decrypt(
            ciphertext,
            padding.OAEP(
                mgf=padding.MGF1(algorithm=hashes.SHA256()),
                algorithm=hashes.SHA256(),
                label=None
            )
        )