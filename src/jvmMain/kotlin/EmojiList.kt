import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp

data class Emoji(val codepoint: String, val raw: String, val name: String) {
    override fun toString(): String {
        return this.raw
    }
}

@Composable
fun EmojiRow(
    emoji: Emoji,
    selected: Boolean,
    selectedBackgroundColor: Color = Color.Unspecified,
    onSelect: (Emoji) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .padding(6.dp)
            .background(
                when (selected) {
                    true -> selectedBackgroundColor
                    else -> Color.Unspecified
                }
            )
            .selectable(selected) { onSelect(emoji) },

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji.name)
        Text(emoji.toString())
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmojiList(
    emojis: List<Emoji>,
    selectedIndex: Int = 0,
    emojiListFocus: FocusRequester,
    onSelect: (Emoji) -> Unit,
    onChangeSelectIndex: (Int) -> Unit,
) {
    Box(modifier = Modifier
        .onPreviewKeyEvent {
            if (it.type == KeyEventType.KeyUp) {
                return@onPreviewKeyEvent false
            }

            when (it.key) {
                Key.Enter -> {
                    onSelect(emojis[selectedIndex])

                    true
                }

                Key.PageDown, Key.DirectionDown -> {
                    onChangeSelectIndex(selectedIndex + 1)

                    true
                }

                Key.PageUp, Key.DirectionUp -> {
                    onChangeSelectIndex(selectedIndex - 1)

                    true
                }

                else -> false
            }
        }
        .focusRequester(emojiListFocus)
        .focusable()
    ) {
        val state = rememberLazyListState()
        val selectedBackgroundColor = MaterialTheme.colors.primaryVariant.copy(0.5f)

        LaunchedEffect(selectedIndex) {
            if (!state.isScrollInProgress) {
                state.scrollToItem(selectedIndex)
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 6.dp).scrollable(state, Orientation.Vertical).fillMaxSize(),
            state = state
        ) {
            items(emojis, { it.codepoint }) {
                EmojiRow(
                    it,
                    emojis.isNotEmpty() && emojis[selectedIndex].codepoint == it.codepoint,
                    selectedBackgroundColor,
                    onSelect
                )
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(), adapter = rememberScrollbarAdapter(state)
        )
    }

    LaunchedEffect(Unit) {
        emojiListFocus.requestFocus()
    }
}