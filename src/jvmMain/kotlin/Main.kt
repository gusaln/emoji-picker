// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun String.codepointToEmoji() = String(this.split(' ').map { it.toInt(16) }.toIntArray(), 0, 1)

data class Emoji(val codepoint: String, val name: String) {
    override fun toString(): String {
        return this.codepoint.codepointToEmoji()
    }
}

val emojiList = listOf(
    Emoji("1F600", "grinning face"),
    Emoji("1F603", "grinning face with big eyes"),
    Emoji("1F604", "grinning face with smiling eyes"),
    Emoji("1F601", "beaming face with smiling eyes"),
    Emoji("1F606", "grinning squinting face"),
    Emoji("1F605", "grinning face with sweat"),
    Emoji("1F923", "rolling on the floor laughing"),
    Emoji("1F602", "face with tears of joy"),
    Emoji("1F642", "slightly smiling face"),
    Emoji("1F643", "upside-down face"),
    Emoji("1FAE0", "melting face"),
    Emoji("1F609", "winking face"),
    Emoji("1F60A", "smiling face with smiling eyes"),
    Emoji("1F607", "smiling face with halo"),
    Emoji("2764 FE0F 200D 1F525", "heart on fire")
)

@Composable
fun EmojiList(emojis: List<Emoji>, onSelect: (Emoji) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 6.dp).fillMaxSize()
    ) {
        emojis
            .forEach {
                Row(
                    modifier = Modifier.padding(4.dp).clickable { onSelect(it) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(it.name)
                    Text(it.toString())
                }
            }
    }
}


@Composable
@Preview
fun App() {
    val clipboardManager = LocalClipboardManager.current
    var query by remember { mutableStateOf("") }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Emojis") })
            },

            content = {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = query,
                        onValueChange = { query = it.trimStart().lowercase() },
                        label = { Text("Search an emoji") }
                    )

                    EmojiList(
                        emojiList.filter { query.isEmpty() || it.name.contains(query.trimEnd()) }
                    )
                    {
                        clipboardManager.setText(AnnotatedString(it.toString()))
                    }
                }
            },
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Emoji picker") {
        App()
    }
}
