package me.gingerninja.sample.lazylistanimations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import me.gingerninja.sample.lazylistanimations.ui.theme.SampleLazyListAnimationsTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val options = remember {
                AnimationSettings()
            }

            var showSettings by rememberSaveable {
                mutableStateOf(false)
            }

            var listType by rememberSaveable {
                mutableStateOf(ListType.COLUMN)
            }

            var counter by remember {
                mutableIntStateOf(0)
            }

            val items = remember {
                mutableStateListOf<Item>()
            }

            SampleLazyListAnimationsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "LazyListAnimations") },
                            actions = {
                                IconButton(
                                    onClick = {
                                        items.add(
                                            Item(
                                                text = "Item #${counter++}"
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Default.Add),
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        items.shuffle()
                                    }
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Default.Refresh),
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        showSettings = true
                                    }
                                ) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Default.Settings),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = listType == ListType.COLUMN,
                                onClick = {
                                    listType = ListType.COLUMN
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 0,
                                    count = 2
                                )
                            ) {
                                Text("Column")
                            }

                            SegmentedButton(
                                selected = listType == ListType.ROW,
                                onClick = {
                                    listType = ListType.ROW
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 1,
                                    count = 2
                                )
                            ) {
                                Text("Row")
                            }
                        }

                        when (listType) {
                            ListType.ROW -> AnimatedLazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                options = options,
                                items = items.toList(),
                                onRemove = {
                                    items.remove(it)
                                },
                                addAt = {
                                    items.add(
                                        index = it,
                                        element = Item(
                                            text = "Item #${counter++}"
                                        )
                                    )
                                }
                            )

                            ListType.COLUMN -> AnimatedLazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                options = options,
                                items = items.toList(),
                                onRemove = {
                                    items.remove(it)
                                },
                                addAt = {
                                    items.add(
                                        index = it,
                                        element = Item(
                                            text = "Item #${counter++}"
                                        )
                                    )
                                }
                            )
                        }

                    }
                }

                if (showSettings) {
                    SettingsSheet(
                        options = options,
                        onDismiss = { showSettings = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    options: AnimationSettings,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(
                    WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                )
        ) {
            ListItem(
                modifier = Modifier.clickable { options.fadeIn = !options.fadeIn },
                headlineContent = {
                    Text("Appearance (fade in)")
                },
                trailingContent = {
                    Switch(
                        checked = options.fadeIn,
                        onCheckedChange = { options.fadeIn = !options.fadeIn })
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )

            ListItem(
                modifier = Modifier.clickable { options.placement = !options.placement },
                headlineContent = {
                    Text("Placement (reorder)")
                },
                trailingContent = {
                    Switch(
                        checked = options.placement,
                        onCheckedChange = { options.placement = !options.placement })
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )

            ListItem(
                modifier = Modifier.clickable { options.fadeOut = !options.fadeOut },
                headlineContent = {
                    Text("Disappearance (fade out)")
                },
                trailingContent = {
                    Switch(
                        checked = options.fadeOut,
                        onCheckedChange = { options.fadeOut = !options.fadeOut })
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun AnimatedLazyColumn(
    modifier: Modifier = Modifier,
    options: AnimationSettings,
    items: List<Item>,
    onRemove: (Item) -> Unit,
    addAt: (Int) -> Unit,
) {
    val state = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
            ItemCard(
                item = item,
                onAddBeforeClick = { addAt(index) },
                onAddAfterClick = { addAt(index + 1) },
                onRemoveClick = { onRemove(item) },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .animateItem(
                        fadeInSpec = if (options.fadeIn) {
                            spring(stiffness = Spring.StiffnessMediumLow)
                        } else {
                            null
                        },
                        placementSpec = if (options.placement) {
                            spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )
                        } else {
                            null
                        },
                        fadeOutSpec = if (options.fadeOut) {
                            spring(stiffness = Spring.StiffnessMediumLow)
                        } else {
                            null
                        }
                    )
            )
        }

        // not part of the animation sample, but this opts out of the list maintaining
        // scroll position when adding elements before the first item
        Snapshot.withoutReadObservation {
            state.requestScrollToItem(
                index = state.firstVisibleItemIndex,
                scrollOffset = state.firstVisibleItemScrollOffset
            )
        }
    }
}

@Composable
fun AnimatedLazyRow(
    modifier: Modifier = Modifier,
    options: AnimationSettings,
    items: List<Item>,
    onRemove: (Item) -> Unit,
    addAt: (Int) -> Unit,
) {
    val state = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        state = state
    ) {
        itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
            ItemCard(
                item = item,
                onAddBeforeClick = { addAt(index) },
                onAddAfterClick = { addAt(index + 1) },
                onRemoveClick = { onRemove(item) },
                modifier = Modifier
                    .padding(10.dp)
                    .animateItem(
                        fadeInSpec = if (options.fadeIn) {
                            spring(stiffness = Spring.StiffnessMediumLow)
                        } else {
                            null
                        },
                        placementSpec = if (options.placement) {
                            spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )
                        } else {
                            null
                        },
                        fadeOutSpec = if (options.fadeOut) {
                            spring(stiffness = Spring.StiffnessMediumLow)
                        } else {
                            null
                        }
                    )
            )
        }

        // not part of the animation sample, but this opts out of the list maintaining
        // scroll position when adding elements before the first item
        Snapshot.withoutReadObservation {
            state.requestScrollToItem(
                index = state.firstVisibleItemIndex,
                scrollOffset = state.firstVisibleItemScrollOffset
            )
        }
    }
}


@Composable
fun ItemCard(
    item: Item,
    onAddBeforeClick: () -> Unit,
    onAddAfterClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                text = item.text,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                text = item.id,
                style = MaterialTheme.typography.labelSmall
            )

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onAddBeforeClick
                ) {
                    Text(text = "Add before")
                }

                TextButton(
                    onClick = onAddAfterClick
                ) {
                    Text(text = "Add after")
                }

                TextButton(
                    onClick = onRemoveClick
                ) {
                    Text(text = "Remove")
                }
            }

        }
    }
}

@Immutable
data class Item(
    val text: String,
    val id: String = UUID.randomUUID().toString(),
)

@Stable
class AnimationSettings {
    var fadeIn by mutableStateOf(true)

    var placement by mutableStateOf(true)

    var fadeOut by mutableStateOf(true)
}

enum class ListType {
    ROW, COLUMN
}