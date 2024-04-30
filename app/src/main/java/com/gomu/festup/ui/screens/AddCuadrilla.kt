package com.gomu.festup.ui.screens

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R


@Composable
fun AddCuadrilla(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nueva cuadrilla",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
        )



        val profilePicture = painterResource(id = R.drawable.ic_launcher_background)
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(Modifier.padding(16.dp)) {
                // Mientras no este la imagen mostrar una "cargando"
                if (profilePicture == null) {
                    LoadingImagePlaceholder(size = 120.dp)
                } else {
                    Image(
                        //bitmap = profilePicture.asImageBitmap(),
                        painter = profilePicture,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                    )
                    // Imagen redonda o cuadrada??
                }
            }
            // Icono para editar imagen
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { /*TODO */})
            ) {
                //Añadir circle y edit
                Icon(painterResource(id = R.drawable.circle), contentDescription = null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Icon(painterResource(id = R.drawable.edit), contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.surface)
            }
        }

        // Form
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Nombre de usuario")},
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = { it},
            label = { Text("Descripcción")},
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        )

        OutlinedTextField(
            value = "",
            onValueChange = {  },
            label = { Text("Localización")},
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp)
        )

        // Create button
        Button(
            onClick = { Log.d("Crearr","cuadrilla")},
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.End),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Crear")
        }
    }
}

@Composable
private fun LoadingImagePlaceholder(size: Dp = 100.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Image(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .alpha(alpha).padding(16.dp),
        painter = painterResource(id = R.drawable.load),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNewTeamScreen() {
    AddCuadrilla(rememberNavController())
}