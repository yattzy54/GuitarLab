package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TremoloBarBottomSheet(
    initialValue: Int = 0,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var barValue by remember { mutableStateOf(initialValue.toFloat()) }

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
                text = "Edit Tremolo Bar",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Value: ${barValue.toInt()}",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = barValue,
                onValueChange = { barValue = it },
                valueRange = -12f..12f,
                steps = 23,
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
                        onSave(barValue.toInt())
                        onDismiss()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}
