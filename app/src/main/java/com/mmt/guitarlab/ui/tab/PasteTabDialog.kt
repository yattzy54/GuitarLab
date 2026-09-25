package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg

@Composable
 fun PasteTabDialog(
    onParse: (text: String, title: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Pasted Tab") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Paste ASCII Tab") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tab Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Tab Text / ASCII") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onParse(text, title) },
                enabled = text.isNotBlank(),
            ) {
                Text("Parse & Load")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PasteTabDialogPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(480.dp),
            contentAlignment = Alignment.Center,
        ) {
            PasteTabDialog(
                onParse = { _, _ -> },
                onDismiss = {}
            )
        }
    }
}