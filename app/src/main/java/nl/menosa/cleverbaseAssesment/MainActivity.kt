package nl.menosa.cleverbaseAssesment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.menosa.cleverbaseAssesment.crypto.CryptoManager
import nl.menosa.cleverbaseAssesment.crypto.CryptoViewModel
import nl.menosa.cleverbaseAssesment.crypto.ui.CryptoScreen
import nl.menosa.cleverbaseAssesment.friendlist.FriendListRepository
import nl.menosa.cleverbaseAssesment.ui.theme.CleverBaseAssesmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cryptoManager = CryptoManager()
        val repository = FriendListRepository(applicationContext)

        // Copied this piece from the internet since this is just overhead.
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CryptoViewModel(cryptoManager, repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        enableEdgeToEdge()
        setContent {
            CleverBaseAssesmentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CryptoScreen(
                        Modifier.padding(innerPadding),
                        viewModel = viewModel(factory = viewModelFactory)
                    )
                }
            }
        }
    }
}