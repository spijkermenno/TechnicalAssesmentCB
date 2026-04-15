package nl.menosa.cleverbaseAssesment.crypto

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nl.menosa.cleverbaseAssesment.friendlist.Friend
import nl.menosa.cleverbaseAssesment.friendlist.FriendListRepository
import nl.menosa.cleverbaseAssesment.friendlist.SaveResult

class CryptoViewModel(
    private val cryptoManager: CryptoManager,
    private val friendListRepository: FriendListRepository
) : ViewModel() {
    var personalPublicKey by mutableStateOf<String?>(null)
    var friends by mutableStateOf<List<Friend>>(emptyList())

    var encryptedText by mutableStateOf("")
    var decryptedText by mutableStateOf("")

    init {
        refreshPublicKey()
        refreshFriends()
    }

    fun generateKeys() {
        cryptoManager.generateKeyPairsIfNeeded()
        refreshPublicKey()
    }

    /** @return true if ciphertext was produced, false on failure */
    fun encrypt(key: String, message: String): Boolean {
        val result = cryptoManager.encryptWithOtherPublicKey(key, message)
        return if (result != null) {
            encryptedText = result
            true
        } else {
            encryptedText = ""
            false
        }
    }

    fun decrypt(encodedText: String) {
        val trimmed = encodedText.trim()
        decryptedText =
            cryptoManager.decrypt(trimmed) ?: "Something went wrong while decrypting..."
    }

    private fun refreshPublicKey() {
        personalPublicKey = cryptoManager.getPublicKeyBase64()
    }

    fun addFriend(nickname: String, publicKeyBase64: String): SaveResult {
        val result = friendListRepository.saveFriend(nickname, publicKeyBase64)
        if (result is SaveResult.Success) {
            refreshFriends()
        }
        return result
    }

    fun removeFriend(friendId: String) {
        friendListRepository.removeFriend(friendId)
        refreshFriends()
    }

    private fun refreshFriends() {
        friends = friendListRepository.getAllFriends()
    }
}