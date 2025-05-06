package fr.marioswitch.tlge2friendcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.marioswitch.tlge2friendcode.ui.theme.TLGE2FriendCodeTheme
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TLGE2FriendCodeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var friendCodeNormal by remember { mutableStateOf("") }
    var friendCodeHard by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter your username") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val normalized = normalizeUsername(username.text)
            friendCodeNormal = md5("${normalized}_pantoufle")
            friendCodeHard = md5("${normalized}_choucroute")
        }) {
            Text("Generate friend codes")
        }

        if (friendCodeNormal.isNotEmpty() || friendCodeHard.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp)
        }

        if (friendCodeNormal.isNotEmpty() && friendCodeHard.isNotEmpty()) {
            Text("Friend code (Normal mode):", style = MaterialTheme.typography.titleMedium)
            Text(friendCodeNormal)

            Spacer(modifier = Modifier.height(8.dp))

            Text("Friend code (Hard mode):", style = MaterialTheme.typography.titleMedium)
            Text(friendCodeHard)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(friendCodeNormal))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy normal")
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(friendCodeHard))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy hard")
                }
            }
        }
    }
}

// Normalize the username: lowercase, remove non-alphanumerics
fun normalizeUsername(input: String): String {
    return input
        .lowercase()
        .filter { it.isLetterOrDigit() }
}

// Generate MD5 hash
fun md5(input: String): String {
    val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TLGE2FriendCodeTheme {
        MainScreen()
    }
}
