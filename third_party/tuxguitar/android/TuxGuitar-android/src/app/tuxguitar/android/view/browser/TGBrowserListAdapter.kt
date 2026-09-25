package app.tuxguitar.android.view.browser

import android.content.Context
import android.content.res.TypedArray
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.browser.model.TGBrowserElement

@Composable
fun TGBrowserElementList(
    elements: List<TGBrowserElement>,
    onElementClick: (TGBrowserElement) -> Unit,
) {
    val folderIconResId = rememberBrowserElementIconResId(R.style.browserElementIconFolderStyle)
    val fileIconResId = rememberBrowserElementIconResId(R.style.browserElementIconFileStyle)

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(elements, key = { it.getName() }) { element ->
            val iconResId = if (element.isFolder()) folderIconResId else fileIconResId
            TGBrowserListItemContent(
                name = element.getName(),
                iconResId = iconResId,
                onClick = { onElementClick(element) },
            )
        }
    }
}

@Composable
fun TGBrowserListItemContent(
    name: String,
    iconResId: Int,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                maxLines = 1,
            )
        }
        HorizontalDivider()
    }
}

@Composable
private fun rememberBrowserElementIconResId(styleResId: Int): Int {
    val context = LocalContext.current
    return remember(context, styleResId) {
        context.findStyledDrawableResource(styleResId)
    }
}

private fun Context.findStyledDrawableResource(styleResId: Int): Int {
    val typedArray: TypedArray = obtainStyledAttributes(styleResId, intArrayOf(android.R.attr.src))
    try {
        return typedArray.getResourceId(0, 0)
    } finally {
        typedArray.recycle()
    }
}

@Preview(showBackground = true)
@Composable
private fun TGBrowserListItemContentPreview() {
    MaterialTheme {
        Column {
            TGBrowserListItemContent(
                name = "Demo",
                iconResId = R.drawable.browser_folder,
                onClick = {},
            )
            TGBrowserListItemContent(
                name = "demo.tg",
                iconResId = R.drawable.browser_file,
                onClick = {},
            )
        }
    }
}
