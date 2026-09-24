package com.mmt.guitarlab.ui.guitartabedit

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.mmt.guitarlab.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGReaderActivity

@Composable
fun TuxGuitarScreen() {
    TuxGuitarLaunchScreen(
        activityClass = TGActivity::class.java,
        autoLaunch = true,
    )
}

@Composable
fun TuxGuitarReaderScreen() {
    TuxGuitarLaunchScreen(
        activityClass = TGReaderActivity::class.java,
        autoLaunch = false,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0F141C)
@Composable
private fun TuxGuitarReaderScreenPreview() {
    TuxGuitarReaderScreen()
}

@Composable
private fun TuxGuitarLaunchScreen(
    activityClass: Class<out TGActivity>,
    autoLaunch: Boolean,
) {
    val context = LocalContext.current

    fun launchTuxGuitar() {
        context.startActivity(
            Intent().setComponent(
                ComponentName(context, activityClass)
            )
        )
    }

    if (autoLaunch) {
        LaunchedEffect(Unit) {
            launchTuxGuitar()
        }
    }

    if (autoLaunch) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F141C)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.tab_tuxguitar),
                color = Color.White,
            )
        }
    } else {
        Scaffold(
            containerColor = Color(0xFF0F141C),
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.tab_tuxguitar_reader),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.tuxguitar_reader_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                )
                Button(onClick = ::launchTuxGuitar) {
                    Text(stringResource(R.string.open_tuxguitar_reader))
                }
            }
        }
    }
}
