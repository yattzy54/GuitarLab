package app.tuxguitar.android.drawer.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.tuxguitar.android.R

@Composable
fun TGMainDrawerContent(
    selectedTabId: Int,
    onTabSelected: (Int) -> Unit,
    fileActions: List<TGMainDrawerFileAction>,
    trackItems: List<TGMainDrawerTrackListItem>,
    onFileActionClick: (TGMainDrawerFileAction) -> Unit,
    onTrackClick: (TGMainDrawerTrackListItem) -> Unit,
    onTrackLongClick: (TGMainDrawerTrackListItem) -> Unit,
    onAddTrackClick: () -> Unit,
) {
    val backgroundColor = colorResource(R.color.darkestColor)
    val mediumColor = colorResource(R.color.mediumColor)
    val textColor = colorResource(R.color.lightestColor)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        TabRow(
            selectedTabIndex = if (selectedTabId == R.id.main_drawer_track_tab) 1 else 0,
            containerColor = mediumColor,
            contentColor = textColor,
        ) {
            Tab(
                selected = selectedTabId == R.id.main_drawer_file_tab,
                onClick = { onTabSelected(R.id.main_drawer_file_tab) },
                text = { Text(stringResource(R.string.main_drawer_file)) },
                selectedContentColor = textColor,
                unselectedContentColor = textColor.copy(alpha = 0.75f),
            )
            Tab(
                selected = selectedTabId == R.id.main_drawer_track_tab,
                onClick = { onTabSelected(R.id.main_drawer_track_tab) },
                text = { Text(stringResource(R.string.main_drawer_tracks)) },
                selectedContentColor = textColor,
                unselectedContentColor = textColor.copy(alpha = 0.75f),
            )
        }

        when (selectedTabId) {
            R.id.main_drawer_track_tab -> TGMainDrawerTrackTab(
                items = trackItems,
                onItemClick = onTrackClick,
                onItemLongClick = onTrackLongClick,
                onAddTrackClick = onAddTrackClick,
                backgroundColor = backgroundColor,
                dividerColor = mediumColor,
                textColor = textColor,
                modifier = Modifier.weight(1f),
            )

            else -> TGMainDrawerFileTab(
                actions = fileActions,
                onActionClick = onFileActionClick,
                backgroundColor = backgroundColor,
                dividerColor = mediumColor,
                textColor = textColor,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TGMainDrawerFileTab(
    actions: List<TGMainDrawerFileAction>,
    onActionClick: (TGMainDrawerFileAction) -> Unit,
    backgroundColor: Color,
    dividerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        items(actions, key = { it.label }) { action ->
            TGMainDrawerFileActionItem(
                label = stringResource(action.label),
                onClick = { onActionClick(action) },
                backgroundColor = backgroundColor,
                textColor = textColor,
            )
            HorizontalDivider(color = dividerColor)
        }
    }
}

@Composable
private fun TGMainDrawerFileActionItem(
    label: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}

@Composable
private fun TGMainDrawerTrackTab(
    items: List<TGMainDrawerTrackListItem>,
    onItemClick: (TGMainDrawerTrackListItem) -> Unit,
    onItemLongClick: (TGMainDrawerTrackListItem) -> Unit,
    onAddTrackClick: () -> Unit,
    backgroundColor: Color,
    dividerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            items(items, key = { it.track.number }) { item ->
                TGMainDrawerTrackListItemContent(
                    label = item.label,
                    selected = item.selected,
                    onClick = { onItemClick(item) },
                    onLongClick = { onItemLongClick(item) },
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                )
                HorizontalDivider(color = dividerColor)
            }
        }
        FloatingActionButton(
            onClick = onAddTrackClick,
            containerColor = dividerColor,
            contentColor = textColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.action_track_add),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TGMainDrawerTrackListItemContent(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (selected) "✓" else "",
            color = textColor,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = label,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGMainDrawerFileTabPreview() {
    MaterialTheme {
        Box(modifier = Modifier.background(colorResource(R.color.darkestColor))) {
            LazyColumn {
                items(
                    listOf(
                        "New",
                        "Open",
                        "Save",
                        "Save as",
                    ),
                ) { label ->
                    TGMainDrawerFileActionItem(
                        label = label,
                        onClick = {},
                        backgroundColor = colorResource(R.color.darkestColor),
                        textColor = colorResource(R.color.lightestColor),
                    )
                    HorizontalDivider(color = colorResource(R.color.mediumColor))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGMainDrawerTrackListItemSelectedPreview() {
    MaterialTheme {
        TGMainDrawerTrackListItemContent(
            label = "Lead Guitar",
            selected = true,
            onClick = {},
            onLongClick = {},
            backgroundColor = colorResource(R.color.darkestColor),
            textColor = colorResource(R.color.lightestColor),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGMainDrawerTrackListItemUnselectedPreview() {
    MaterialTheme {
        TGMainDrawerTrackListItemContent(
            label = "Bass",
            selected = false,
            onClick = {},
            onLongClick = {},
            backgroundColor = colorResource(R.color.darkestColor),
            textColor = colorResource(R.color.lightestColor),
        )
    }
}
