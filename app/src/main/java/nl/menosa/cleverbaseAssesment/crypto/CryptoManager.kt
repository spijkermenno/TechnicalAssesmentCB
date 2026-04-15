package nl.menosa.cleverbaseAssesment.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class CryptoManager {
    // Load Native AndroidKeyStore with password null.
    private val keystore =
        KeyStore.getInstance(KEYSTORE_IDENTIFIER).apply { load(null) }

    // Key generation
    fun generateKeyPairsIfNeeded() {
        if (!keystore.containsAlias(KEYGEN_ALIAS)) {
            generateKeyPairs()
        }
    }

    private fun generateKeyPairs() {
        // Get key pair generator for algorithm RSA, for AndroidKeyStore.
        val keyPairGenerator =
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_IDENTIFIER)

        /** Create keygen specs that will be used in the keyPairGenerator.
         *  Set padding for salting
         *  Digests is used for quick checking integrity
         */
        val keyGenParameterSpec =
            KeyGenParameterSpec
                .Builder(
                    KEYGEN_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_SIGN
                )
                .setKeySize(KEY_SIZE)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build()

        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()

    }

    fun getPublicKey(): ByteArray? = keystore.getCertificate(KEYGEN_ALIAS)?.publicKey?.encoded

    fun getPublicKeyBase64(): String? = getPublicKey()?.toBase64()

    // Cryptography
    fun encrypt(text: String): String? {
        /**
         * transform the publicKeyBytesArray to a x509 public key.
         */
        val publicKeyBytes = getPublicKey() ?: return null
        val x509keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        val publicKey = keyFactory.generatePublic(x509keySpec)

        try {
            val cipher: Cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            return cipher.doFinal(text.toByteArray()).toBase64()
        } catch (e: Exception) {
            // Handle error later in UI.
            return null
        }
    }

    fun decrypt(base64text: String): String? {
        try {
            val privateKey: PrivateKey = keystore.getKey(KEYGEN_ALIAS, null) as PrivateKey
            val cipher: Cipher = Cipher.getInstance(ENCRYPTION_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            return cipher.doFinal(base64text.decodeBase64()).toString(Charsets.UTF_8)
        } catch (e: Exception) {
            // Handle error later in UI.
            return null
        }
    }

    companion object {
        const val KEYSTORE_IDENTIFIER = "AndroidKeyStore"
        const val KEYGEN_ALIAS = "CleverBaseAssesment"

        // Combination of the chosen encryption algorithm and padding.
        private const val ENCRYPTION_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val KEY_SIZE = 2048
    }

    // Helper methods
    private fun ByteArray.toBase64() = Base64.getEncoder().encodeToString(this)
    private fun String.decodeBase64() = Base64.getDecoder().decode(this)
}
