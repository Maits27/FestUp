package com.gomu.festup.ui.elements.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.gradientsColors

@Composable
fun FestUpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = { onClick() },
        enabled = enabled,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(colors = gradientsColors)
        ),
        modifier = modifier,
        content = content
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, )
@Composable
fun FestUpButtonPreview() {
    FestUpButton(onClick = { /*TODO*/ }, modifier = Modifier) {
        Text(text = "Prueba")
    }
}