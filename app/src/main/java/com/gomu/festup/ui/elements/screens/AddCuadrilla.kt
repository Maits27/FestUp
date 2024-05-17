package com.gomu.festup.ui.elements.screens

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.compose.FestUpTheme
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.localUriToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Pantalla para añadir una [Cuadrilla] a la aplicación
 * (aparece tanto en horizontal como en vertical).
 */
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AddCuadrilla(navController: NavController, mainVM: MainVM) {

    val context = LocalContext.current
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

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

    // Comprobación del formulario y de los datos añadidos
    val onCreateClick: () -> Unit = {
        if (nombre.isEmpty()) Toast.makeText(context, context.getString(R.string.insert_nombre), Toast.LENGTH_SHORT).show()
        else if (descripcion.isEmpty()) Toast.makeText(context, context.getString(R.string.insert_desc), Toast.LENGTH_SHORT).show()
        else if (localizacion.isEmpty()) Toast.makeText(context, context.getString(R.string.insert_loc), Toast.LENGTH_SHORT).show()
        else {
            // Crear cuadrilla
            CoroutineScope(Dispatchers.IO).launch {
                val imageBitmap: Bitmap? = null
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
                        Toast.makeText(context, context.getString(R.string.error_intentalo), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    if (isVertical){
        Column (
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(Modifier.padding(16.dp)) {
                    Imagen(imageUri, R.drawable.no_image, 120.dp) {}
                }
                EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
            }


            // Form
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it.replace(" ", "").replace("\n", "")  },
                label = { Text(stringResource(id = R.string.nombre_cuadrilla))},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text(stringResource(id = R.string.desc))},
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )

            OutlinedTextField(
                value = localizacion,
                onValueChange = { localizacion = it },
                label = { Text(stringResource(id = R.string.loc))},
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Create button
            FestUpButton(
                onClick = { onCreateClick() },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = context.getString(R.string.crear))
            }
        }
    }
    else{
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(Modifier.padding(16.dp)) {
                        Imagen(imageUri, R.drawable.no_image, 120.dp) {}
                    }
                    EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
                }
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 40.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Form
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it.replace(" ", "").replace("\n", "")  },
                    label = { Text(stringResource(id = R.string.nombre_cuadrilla))},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text(stringResource(id = R.string.desc))},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = localizacion,
                    onValueChange = { localizacion = it },
                    label = { Text(stringResource(id = R.string.loc))},
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // Create button
                FestUpButton(
                    onClick = { onCreateClick() },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = context.getString(R.string.crear))
                }
            }
        }
    }
}
