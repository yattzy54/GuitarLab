package app.tuxguitar.android.view.processing

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import android.os.Bundle
import androidx.activity.ComponentDialog
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity

/**
 * Replacement for the deprecated [android.app.ProgressDialog] previously used
 * by [TGActionProcessingView]. Keeps the same show()/dismiss() shaped API so
 * the surrounding polling/threading logic in [TGActionProcessingController]
 * did not need to change.
 */
class TGProcessingDialog(activity: TGActivity) : ComponentDialog(activity.requireContext()) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
        setContentView(
            ComposeView(context).apply {
                setContent {
                    GuitarLabTheme {
                        TGProcessingDialogContent()
                    }
                }
            }
        )
    }
}

@Composable
fun TGProcessingDialogContent() {
    Surface(shape = MaterialTheme.shapes.medium) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.global_processing),
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGProcessingDialogContentPreview() {
    GuitarLabTheme {
        TGProcessingDialogContent()
    }
}
