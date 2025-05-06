package fr.marioswitch.tlge2friendcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
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
        // Display the app name from resources
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input field for the username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.pseudonym)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                // Display the clear icon if the username is not empty
                if (username.text.isNotEmpty()) {
                    IconButton(onClick = { username = TextFieldValue("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear text")
                    }
                }
            }
        )

        // Button to generate the codes
        Button(onClick = {
            val normalized = normalizeUsername(username.text)
            friendCodeNormal = md5("${normalized}_pantoufle")
            friendCodeHard = md5("${normalized}_choucroute")
        }) {
            Text(stringResource(id = R.string.generate_codes))
        }

        // Conditional divider
        if (friendCodeNormal.isNotEmpty() || friendCodeHard.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp)
        }

        // Display the friend codes and buttons
        if (friendCodeNormal.isNotEmpty() && friendCodeHard.isNotEmpty()) {
            Text(stringResource(id = R.string.friend_code_normal), style = MaterialTheme.typography.titleMedium)
            Text(friendCodeNormal)

            Spacer(modifier = Modifier.height(4.dp))

            Text(stringResource(id = R.string.friend_code_hard), style = MaterialTheme.typography.titleMedium)
            Text(friendCodeHard)

            Spacer(modifier = Modifier.height(8.dp))

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
                    Text(stringResource(id = R.string.copy_normal))
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(friendCodeHard))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(id = R.string.copy_hard))
                }
            }
        }
    }
}

// Utility functions
fun normalizeUsername(input: String): String {
    return input
        .lowercase()
        .filter { it.isLetterOrDigit() }
}

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
