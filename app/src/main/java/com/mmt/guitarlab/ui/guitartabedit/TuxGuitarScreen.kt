package com.mmt.guitarlab.ui.guitartabedit

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGReaderActivity

@Composable
fun TuxGuitarScreen() {
    TuxGuitarLaunchScreen(TGActivity::class.java)
}

@Composable
fun TuxGuitarReaderScreen() {
    TuxGuitarLaunchScreen(TGReaderActivity::class.java)
}

@Composable
private fun TuxGuitarLaunchScreen(activityClass: Class<out TGActivity>) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.startActivity(
            Intent().setComponent(
                ComponentName(context, activityClass)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
