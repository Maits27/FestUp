package com.gomu.festup.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R


@Composable
fun PerfilUser(
    navController: NavController,
    username: String = "usuarioBestea"
) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
//            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.tertiary)
                .weight(1f)
        ){
            TopProfile(username = username, email = "user@gmail.com", edad = 18, picture = null, false)
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onTertiary)
                .weight(2f)
        ){
            SeguidoresYSeguidos(username = username)
            ListadoCuadrillas(
                username = username,
                cuadrillas = listOf(
                    Cuadrilla(nombre = "Pikito", descripcion = "The best", lugar = "Bilbao"),
                    Cuadrilla(nombre = "Wekaland", descripcion = "The best 2", lugar = "Bilbao"),
                    Cuadrilla(nombre = "BANBU", descripcion = "The best 4", lugar = "Bilbao")
                ),
                false
            )

        }
    }
}

//@Preview
//@Composable
//fun PerfilPreview(){
//    PerfilUser(navController = rememberNavController())
//}