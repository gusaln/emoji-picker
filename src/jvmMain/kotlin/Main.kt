import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(trayState: TrayState) {
    val clipboardManager = LocalClipboardManager.current
    val emojis = useResource("/emojis.csv") { inputStream ->
        inputStream.bufferedReader().readLines().map { line ->
            val parts = line.split(';').map { it.trim() }

            Emoji(parts[0], parts[1], parts[2])
        }
    }

    var query by remember { mutableStateOf("") }
    val filteredEmojis by remember {
        derivedStateOf {
            emojis.filter {
                query.isEmpty() || it.name.lowercase().contains(query.trimEnd())
            }
        }
    }

    var selectedIndex by remember { mutableStateOf(0) }

    val searchbarFocus = remember { FocusRequester() }
    val emojiListFocus = remember { FocusRequester() }
    var searching by remember { mutableStateOf(false) }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Emojis") })
            },

            content = {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize().onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp) {
                            return@onPreviewKeyEvent false
                        }

                        when (it.key) {
                            Key.Tab -> {
                                if (searching) {
                                    emojiListFocus
                                } else {
                                    searchbarFocus
                                }.requestFocus()

                                true
                            }

                            else -> false
                        }
                    }, verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().focusRequester(searchbarFocus).onFocusChanged {
                            searching = it.hasFocus
                        }.onPreviewKeyEvent {
                            if (it.type == KeyEventType.KeyUp) {
                                return@onPreviewKeyEvent false
                            }

                            when (it.key) {
                                Key.PageDown, Key.DirectionDown -> {

                                    emojiListFocus.requestFocus()

                                    true
                                }

                                else -> false
                            }
                        },
                        label = { Text("Type to filter the list") },
                        value = query,
                        leadingIcon = {
                            Icon(Icons.Default.Search, "Search an emoji")
                        },
                        onValueChange = {
                            query = it.trimStart().lowercase()

                            selectedIndex = 0
                        },
                    )

                    EmojiList(
                        filteredEmojis,
                        selectedIndex,
                        emojiListFocus,

                        onSelect = {
                            println("${it.name} emoji was copied to clipboard")

                            clipboardManager.setText(AnnotatedString(it.toString()))

                            trayState.sendNotification(
                                Notification(
                                    "Emoji Picked!", "${it.name} emoji was copied to clipboard"
                                )
                            )
                        },
                        onChangeSelectIndex = {
                            selectedIndex = if (it < 0) {
                                filteredEmojis.size - 1
                            } else {
                                it % filteredEmojis.size
                            }

                        },
                    )
                }
            },
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CloseWindowDialog(
    onCloseDialog: () -> Unit,
    onCloseWindow: () -> Unit,
    onExitApplication: () -> Unit,
) {
    Dialog(title = "What do you wish to do?", onCloseRequest = onCloseDialog, onPreviewKeyEvent = {
        when (it.key) {
            Key.Escape -> {
                onExitApplication()

                true
            }

            Key.Enter -> {
                onCloseWindow()

                true
            }

            else -> false
        }
    }) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            Row { Text("Do you wish to close the Window (Enter) or exit the application (Esc)?") }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                Button(onClick = onExitApplication) {
                    Text("Exit")
                }

                Button(onClick = onCloseWindow) {
                    Text("Tray")
                }
            }
        }
    }
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(128f, 128f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}

@ExperimentalComposeUiApi
fun main() = application {
    var isVisible by remember { mutableStateOf(true) }
    var isAskingToClose by remember { mutableStateOf(false) }

    val trayState = rememberTrayState()

    Tray(icon = TrayIcon, state = trayState, onAction = { isVisible = true }, menu = {
        Item(
            "Exit", onClick = this@application::exitApplication
        )
    })

    if (isAskingToClose) {
        CloseWindowDialog(onCloseDialog = { isAskingToClose = false }, onCloseWindow = {
            isVisible = false
            isAskingToClose = false
        }, onExitApplication = ::exitApplication
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        onPreviewKeyEvent = {
            when (it.key) {
                Key.Escape -> {
                    isAskingToClose = true

                    true
                }

                else -> false
            }
        },
        title = "Emoji picker",
        visible = isVisible,
    ) {
        App(trayState)
    }
}
