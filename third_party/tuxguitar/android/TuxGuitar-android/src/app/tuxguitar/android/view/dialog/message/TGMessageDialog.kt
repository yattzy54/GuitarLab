package app.tuxguitar.android.view.dialog.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog

class TGMessageDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGMessageDialogContent(
            title = getAttribute(TGMessageDialogController.ATTRIBUTE_TITLE),
            message = getAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE),
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun TGMessageDialogContent(
    title: String?,
    message: String?,
    onDismiss: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        if (!title.isNullOrEmpty()) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
        if (!message.isNullOrEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGMessageDialogContentPreview() {
    MaterialTheme {
        TGMessageDialogContent(
            title = "Import complete",
            message = "The song was imported successfully and is ready to edit.",
            onDismiss = {},
        )
    }
}
