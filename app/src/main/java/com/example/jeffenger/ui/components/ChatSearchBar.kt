package com.example.jeffenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeffenger.ui.theme.UrbanistText
import com.example.jeffenger.utils.debugging.LogComposable

@Composable
fun ChatSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LogComposable("ChatSearchBar") {
        val scheme = MaterialTheme.colorScheme

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    color = scheme.tertiaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = scheme.outline,
                    modifier = Modifier.size(20.dp)
                )

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = UrbanistText.Placeholder.copy(
                        color = scheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Suchen",
                                style = UrbanistText.Placeholder,
                                color = scheme.outline
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

//        OutlinedTextField(
//            value = query,
//            onValueChange = onQueryChange,
//            placeholder = {
//                Text(
//                    text = "Suchen",
//                    color = scheme.outline,
//                    style = UrbanistText.Placeholder
//                )
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Rounded.Search,
//                    contentDescription = "Search",
//                    tint = scheme.outline
//                )
//            },
//            singleLine = true,
//            textStyle = MaterialTheme.typography.bodyMedium,
//            shape = RoundedCornerShape(10.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = scheme.tertiaryContainer,
//                unfocusedContainerColor = scheme.tertiaryContainer,
//                focusedIndicatorColor = scheme.tertiaryContainer,
//                unfocusedIndicatorColor = scheme.tertiaryContainer,
//                cursorColor = scheme.onTertiaryContainer
//            ),
//            modifier = modifier
//                .fillMaxWidth()
//                .height(44.dp),
//            contentPadding = PaddingValues(
//                horizontal = 12.dp,
//                vertical = 0.dp
//            )
//        )
    }
}

//        OutlinedTextField(
//            value = query,
//            onValueChange = onQueryChange,
//            placeholder = {
//                Text(
//                    text = "Suchen",
//                    color = scheme.outline
//                )
//            },
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Outlined.Search,
//                    contentDescription = "Search",
//                    tint = scheme.outline
//                )
//            },
//            singleLine = true,
//            shape = RoundedCornerShape(10.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = scheme.tertiaryContainer,
//                unfocusedContainerColor = scheme.tertiaryContainer,
//                focusedIndicatorColor = scheme.tertiaryContainer,
//                unfocusedIndicatorColor = scheme.tertiaryContainer,
//                cursorColor = scheme.onTertiaryContainer
//            ),
//            modifier = modifier
//                .fillMaxWidth()
//                .height(44.dp)
//        )
