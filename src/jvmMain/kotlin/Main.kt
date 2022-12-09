// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun String.codepointToEmoji() = String(this.split(' ').map { it.toInt(16) }.toIntArray(), 0, 1)

data class Emoji(val codepoint: String, val raw: String, val name: String) {
    override fun toString(): String {
        return this.raw
    }
}

@Composable
fun EmojiList(emojis: List<Emoji>, onSelect: (Emoji) -> Unit) {
    val scrollState = rememberScrollState()

    LazyColumn(Modifier.padding(horizontal = 6.dp).scrollable(scrollState, Orientation.Vertical).fillMaxSize()) {
        items(emojis, { it.codepoint }) {
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


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val emojis = ResourceLoader.Default.load("/emojis.csv")
        .bufferedReader()
        .readLines()
        .map { line ->
            val parts = line.split(';').map { it.trim() }

            Emoji(parts[0], parts[1], parts[2])
        }

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
                        emojis.filter { query.isEmpty() || it.name.lowercase().contains(query.trimEnd()) }
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
