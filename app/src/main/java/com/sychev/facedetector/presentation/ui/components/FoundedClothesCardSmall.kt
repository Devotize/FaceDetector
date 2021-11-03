package com.sychev.facedetector.presentation.ui.components

import android.graphics.RectF
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sychev.facedetector.domain.Clothes
import com.sychev.facedetector.utils.toMoneyString

@Composable
fun FoundedClothesCardSmall(
    location: RectF,
    clothes: Clothes,
    onClick: () -> Unit
) {
    val x = with(LocalDensity.current){location.centerX().toDp()}
    val y = with(LocalDensity.current){location.centerY().toDp()}
    Surface(
        modifier = Modifier
            .offset(x = x - 35.dp, y = y - 10.dp)
            .clickable { onClick() },
        color = Color.Black.copy(alpha = 0.8f),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.
            padding(start = 3.dp, end = 3.dp, top = 1.dp,bottom = 1.dp)
        ) {
            val fullStr = "${clothes.itemCategory} ${clothes.brand}"
            var text = ""
            kotlin.run let@{ fullStr.forEach { char ->
                text += char
                if (text.length > 15) {
                    text += "..."
                    return@let
                }
            } }

            Text(
                modifier = Modifier,
                text = text,
                maxLines = 1,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.caption
            )
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier,
                    text = "${clothes.price.toString().toMoneyString()} ₽",
                    color= MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.caption
                )
                Spacer(modifier = Modifier.width(1.dp))
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primaryVariant
                )
            }
        }

    }
}
