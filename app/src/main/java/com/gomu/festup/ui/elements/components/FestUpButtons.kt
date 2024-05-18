package com.gomu.festup.ui.elements.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.compose.gradientsColors

/**
    EN ESTE FICHERO SE DEFINE UN BOTON PERSONALIZADO CON GRADIENTE QUE SE EMPLEAN EN MULTIPLES PANTALLAS DE LA APLICACION
 **/
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