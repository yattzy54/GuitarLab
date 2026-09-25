package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@Composable
 fun ProjectsDialog(
    projects: List<TabProjectEntity>,
    onSelect: (TabProjectEntity) -> Unit,
    onDelete: (TabProjectEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saved Local Projects", fontWeight = FontWeight.Bold) },
        text = {
            if (projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No saved projects found.")
                }
            } else {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(projects) { project ->
                        Card(
                            onClick = { onSelect(project) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(project.artist, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onDelete(project) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProjectsDialogPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(450.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            ProjectsDialog(
                projects = listOf(
                    TabProjectEntity(
                        id = "123",
                        title = "Heavy Metal Riff",
                        artist = "BuzzÖuter",
                        updatedAt = System.currentTimeMillis(),
                        jsonContent = ""

                    ),
                    TabProjectEntity(
                        id = "124",
                        title = "Acoustic Melody",
                        artist = "Solo",
                        updatedAt = System.currentTimeMillis(),
                        jsonContent = ""
                    )
                ),
                onSelect = {},
                onDelete = {},
                onDismiss = {}
            )
        }
    }
}