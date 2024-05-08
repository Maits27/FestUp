package com.gomu.festup.ui.screens

import android.content.ContentResolver
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.FestUpTheme
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R
import com.gomu.festup.utils.localUriToBitmap
import com.gomu.festup.vm.MainVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AddCuadrilla(navController: NavController, mainVM: MainVM) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var localizacion by remember { mutableStateOf("") }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    Column (
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(40.dp)
        //.verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
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


        Box(contentAlignment = Alignment.BottomEnd) {
            Box(Modifier.padding(16.dp)) {
                // Mientras no este la imagen mostrar una "cargando"
                //LoadingImagePlaceholder(size = 120.dp)
                if (imageUri == null) {
                    AsyncImage(
                        model = "http://34.16.74.167/cuadrillaProfileImages/no-cuadrilla.png",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                    )
                }
                else {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                    )
                }
            }
            // Icono para editar imagen
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .clip(CircleShape)
                    .clickable(onClick = {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    })
            ) {
                // Añadir circle y edit
                Icon(painterResource(id = R.drawable.circle), contentDescription = null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Icon(painterResource(id = R.drawable.edit), contentDescription = null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.surface)
            }
        }

        // Form
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de cuadrilla")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        )

        OutlinedTextField(
            value = localizacion,
            onValueChange = { localizacion = it },
            label = { Text("Localización")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        )

        // Create button
        Button(
            onClick = {
                if (nombre.isEmpty()) {
                    Toast.makeText(context, "Inserta un nombre", Toast.LENGTH_SHORT).show()
                } else if (descripcion.isEmpty()) {
                    Toast.makeText(context, "Inserta una descripción", Toast.LENGTH_SHORT).show()
                } else if (localizacion.isEmpty()) {
                    Toast.makeText(context, "Inserta una localización", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear cuadrilla
                    CoroutineScope(Dispatchers.IO).launch {
                        var imageBitmap: Bitmap? = null
                        if (imageUri != null) context.localUriToBitmap(imageUri!!)

                        val insertCorrecto = withContext(Dispatchers.IO) {
                            mainVM.crearCuadrilla(
                                cuadrilla = Cuadrilla(
                                    nombre = nombre,
                                    lugar = localizacion,
                                    descripcion = descripcion
                                ),
                                image = imageBitmap)
                        }
                        if (insertCorrecto) {
                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Ha ocurrido un error, inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // Foto de perfil TODO
                    //mainVM.setCuadrillaProfile(context, imageUri, nombre)
                }
            },
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
fun LoadingImagePlaceholder(size: Dp = 100.dp) {
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
            .alpha(alpha)
            .padding(16.dp),
        painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
fun PreviewNewTeamScreen() {
    FestUpTheme {
        AddCuadrilla(rememberNavController(), hiltViewModel())
    }
}