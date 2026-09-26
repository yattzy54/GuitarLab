package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BendEditorBottomSheet(
    initialValue: Int = 0,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var bendValue by remember { mutableStateOf(initialValue.toFloat()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Bend",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bend Value: ${bendValue.toInt()}",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = bendValue,
                onValueChange = { bendValue = it },
                valueRange = 0f..12f,
                steps = 11,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onSave(bendValue.toInt())
                        onDismiss()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}
