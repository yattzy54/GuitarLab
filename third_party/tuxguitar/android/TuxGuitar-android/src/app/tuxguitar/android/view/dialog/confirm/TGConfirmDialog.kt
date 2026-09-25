package app.tuxguitar.android.view.dialog.confirm

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import app.tuxguitar.android.ui.state.scopedEditorViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog

class TGConfirmDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGConfirmDialogContent(
            message = getMessage().orEmpty(),
            onConfirm = {
                onSuccess()
                onDismiss()
            },
            onCancel = {
                onCancel()
                onDismiss()
            },
        )
    }

    fun onSuccess() {
        getRunnable()?.run()
    }

    fun onCancel() {
        getCancelRunnable()?.run()
    }

    fun getMessage(): String? = getAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE)

    fun getRunnable(): Runnable? = getAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE)

    fun getCancelRunnable(): Runnable? =
        getAttribute(TGConfirmDialogController.ATTRIBUTE_CANCEL_RUNNABLE)
}

@Composable
fun TGConfirmDialogContent(
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel = scopedEditorViewModel { TGConfirmDialogViewModel() }
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.confirm_dlg_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { viewModel.decide(TGConfirmationDecision.CANCEL, onCancel) }) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = { viewModel.decide(TGConfirmationDecision.CONFIRM, onConfirm) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGConfirmDialogContentPreview() {
    GuitarLabTheme {
        TGConfirmDialogContent(
            message = "Are you sure you want to delete this track? This action cannot be undone.",
            onConfirm = {},
            onCancel = {},
        )
    }
}
