package com.sychev.facedetector.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.utils.TAG

@Composable
fun AppTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onStartAssistant: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp, 16.dp, 16.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.70f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant),
        ) {
            BasicTextField(
                modifier = Modifier
                    .height(40.dp)
                    .padding(12.dp, 0.dp, 0.dp, 0.dp),
                value = query,
                onValueChange = onQueryChange,
                decorationBox = {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(0.8f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box() {
                                //hint
                                if (query.isEmpty()) Text(text = "Search", color = MaterialTheme.colors.primaryVariant)
                                it()
                            }
                        }
                        Icon(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                        )
                    }
                },
//                trailingIcon = {
//                    Icon(
//                        imageVector = Icons.Outlined.Search,
//                        contentDescription = null,
//                        tint = MaterialTheme.colors.onSurface
//                    )
//                },
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
//                keyboardActions = KeyboardActions {
//                    Log.d(TAG, "onCreateView: keyboard action performed")
//                },
//                colors = TextFieldDefaults.textFieldColors(
//                    textColor = MaterialTheme.colors.onPrimary,
//                    backgroundColor = MaterialTheme.colors.surface,
//                    focusedIndicatorColor = MaterialTheme.colors.secondary,
//                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant,
//                    focusedLabelColor = MaterialTheme.colors.secondary,
//                    cursorColor = MaterialTheme.colors.onPrimary,
//                ),
//                shape = MaterialTheme.shapes.large,
                singleLine = true
            )
        }
        Surface(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
        ) {
            IconButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    onStartAssistant()
                }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(0.55f),
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }

        Surface(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.surface,
            border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant)
        ) {
            IconButton(
                modifier = Modifier.fillMaxSize(),
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(0.55f),
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }


    }
}