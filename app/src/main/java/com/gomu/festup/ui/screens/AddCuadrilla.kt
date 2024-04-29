package com.gomu.festup.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla


@Composable
fun AddCuadrilla(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Nueva cuadrilla",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
        )

        // Image
        val imageModifier = Modifier
            .fillMaxWidth()
            .height(200.dp).padding(20.dp)
            .clickable(onClick = {})
        Image(
            painter = ColorPainter(Color.Gray),
            contentDescription = "Clickable Image",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )


        // Form
        Text(
            text = "Nombre:",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = "",
            onValueChange = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo "Descripción"
        Text(
            text = "Descripción:",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        TextField(
            value = "Cuadrilla de amigos que nos gusta...",
            onValueChange = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo "Localización"
        Text(
            text = "Localización:",
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        TextField(
            value = "Bilbao",
            onValueChange = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        )

        // Create button
        Button(
            onClick = { Log.d("Crearr","cuadrilla")},
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Crear")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewTeamScreen() {
    AddCuadrilla(rememberNavController())
}