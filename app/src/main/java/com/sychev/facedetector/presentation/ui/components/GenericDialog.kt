package com.sychev.facedetector.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onPositiveAction: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
                Text(text = title)
                },
        text = {
            Text(text = message)
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    ),
                    onClick = onPositiveAction,
                ) {
                    Text(text = "OK")
                }
            }
        }
    )
}