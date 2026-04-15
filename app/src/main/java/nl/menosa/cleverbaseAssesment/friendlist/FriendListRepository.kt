package nl.menosa.cleverbaseAssesment.friendlist

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID

class FriendListRepository(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("friend_list", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllFriends(): List<Friend> {
        val friendsJson = sharedPreferences.getString(FRIENDLIST_KEY, null) ?: return emptyList()

        return try {
            json.decodeFromString<List<Friend>>(friendsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveFriend(name: String, publicKeyBase64: String): SaveResult {
        val friend = Friend(UUID.randomUUID().toString(), name, publicKeyBase64)
        val friendList = getAllFriends().toMutableList()

        if (friendList.find { it.nickname == friend.nickname } != null) {
            return SaveResult.Error("This nickname is already taken.")
        }

        friendList.add(friend)
        val jsonString = json.encodeToString(friendList)
        sharedPreferences.edit { putString(FRIENDLIST_KEY, jsonString) }
        return SaveResult.Success(friend)
    }

    fun removeFriend(friendId: String) {
        val friendList = getAllFriends().toMutableList()

        val friendToRemove = friendList.find { it.id == friendId }

        if (friendToRemove != null) {
            friendList.remove(friendToRemove)
            val jsonString = json.encodeToString(friendList)
            sharedPreferences.edit { putString(FRIENDLIST_KEY, jsonString) }
        }
    }

    companion object {
        private const val FRIENDLIST_KEY = "friends_list"
    }
}

@Serializable
data class Friend(
    val id: String,
    val nickname: String,
    val publicKeyBase64: String
)

sealed class SaveResult {
    data class Success(val friend: Friend) : SaveResult()
    data class Error(val message: String) : SaveResult()
}