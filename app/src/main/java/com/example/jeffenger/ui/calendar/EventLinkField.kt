package com.example.jeffenger.ui.calendar

import android.net.Uri
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun EventLinkField(
    link: String,
    label: String,
    modifier: Modifier = Modifier
) {
    LogComposable("EventLinkField") {

        val scheme = MaterialTheme.colorScheme
        val context = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {

            Text(
                text = label,
                style = UrbanistText.Label,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .background(
                        scheme.tertiaryContainer,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {

                if (link.isBlank()) {
                    Text(
                        text = "Kein Meeting Link",
                        style = UrbanistText.Placeholder,
                        color = scheme.outline
                    )
                } else {
                    val isUrl =
                        android.util.Patterns.WEB_URL.matcher(link).matches()

                    Text(
                        text = link,
                        style = UrbanistText.BodyRegular,
                        color = if (isUrl) scheme.primary else scheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable(enabled = isUrl) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}