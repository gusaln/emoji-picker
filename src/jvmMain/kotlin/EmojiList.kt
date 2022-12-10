import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
            .clip(RoundedCornerShape(4.dp))
            .padding(6.dp)
            .background(
                when (selected) {
                    true -> selectedBackgroundColor
                    else -> Color.Unspecified
                }
            )
            .fillMaxWidth()
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
fun EmojiList(emojis: List<Emoji>, onSelect: (Emoji) -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    val selected by remember { derivedStateOf { emojis[selectedIndex] } }
    val selectedBackgroundColor = MaterialTheme.colors.secondary.copy(0.7f)

    Box {
        val state = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.padding(horizontal = 6.dp).scrollable(state, Orientation.Vertical).fillMaxSize(),
            state = state
        ) {
            items(emojis, { it.codepoint }) {
                EmojiRow(it, selected.codepoint == it.codepoint, selectedBackgroundColor, onSelect)
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(), adapter = rememberScrollbarAdapter(state)
        )
    }
}