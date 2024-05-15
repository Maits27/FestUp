package com.gomu.festup.ui.elements.screens

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gomu.festup.R
import com.gomu.festup.data.localDatabase.Entities.Cuadrilla
import com.gomu.festup.data.localDatabase.Entities.Usuario
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.elements.components.EditImageIcon
import com.gomu.festup.ui.elements.components.FestUpButton
import com.gomu.festup.ui.elements.components.Imagen
import com.gomu.festup.ui.elements.components.cards.EventoCard
import com.gomu.festup.ui.elements.components.cards.EventoMiniCard
import com.gomu.festup.ui.elements.components.cards.UsuarioMiniCard
import com.gomu.festup.ui.elements.components.dialogs.EstasSeguroDialog
import com.gomu.festup.ui.vm.MainVM
import com.gomu.festup.utils.openTelegram
import com.gomu.festup.utils.openWhatsApp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.AccessController.getContext


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PerfilCuadrilla(
    navController: NavController,
    mainVM: MainVM
) {
    val cuadrilla= mainVM.cuadrillaMostrar.value!!
    val usuariosCuadrilla = mainVM.usuariosCuadrilla().collectAsState(initial = emptyList())

    val integrante = mainVM.getIntegrante(cuadrilla, mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    val pertenezco = integrante.value.isNotEmpty()

    var refresh by remember{ mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val refreshState = rememberPullRefreshState(
        refreshing = refresh,
        onRefresh = {
            CoroutineScope(Dispatchers.IO).launch{
                refresh = true
                mainVM.actualizarDatos()
                refresh = false
            }
        },
    )

    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isVertical){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if (pertenezco){ EliminarCuadrilla(navController, mainVM = mainVM, cuadrilla = cuadrilla) }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(
                            scrollState,
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TopProfileCuadrilla(
                        mainVM =  mainVM,
                        cuadrilla = cuadrilla,
                        pertenezco = pertenezco,
                        navController = navController
                    )
                    PullRefreshIndicator(
                        refreshing = refresh,
                        state = refreshState,
                        modifier = Modifier.align(
                            Alignment.TopCenter,
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ListadoUsuarios(
                        usuarios = usuariosCuadrilla.value,
                        pertenezco,
                        mainVM,
                        navController,
                        numeroIntegrantes = usuariosCuadrilla.value.size
                    )
                    EventosCuadrilla(
                        mainVM = mainVM,
                        navController = navController,
                        cuadrilla = cuadrilla
                    )
                }
            }
        }
    }
    else{
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .pullRefresh(refreshState)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {

                    TopProfileCuadrilla(
                        mainVM = mainVM,
                        cuadrilla = cuadrilla,
                        pertenezco = pertenezco,
                        navController = navController
                    )

                    PullRefreshIndicator(
                        refreshing = refresh,
                        state = refreshState,
                        modifier = Modifier.align(
                            Alignment.TopCenter,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (pertenezco) {
                    EliminarCuadrilla(navController, mainVM = mainVM, cuadrilla = cuadrilla)
                }
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp, 0.dp, 0.dp, 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                ListadoUsuarios(
                    usuarios = usuariosCuadrilla.value,
                    pertenezco,
                    mainVM,
                    navController,
                    numeroIntegrantes = usuariosCuadrilla.value.size
                )
                EventosCuadrilla(
                    mainVM = mainVM,
                    navController = navController,
                    cuadrilla = cuadrilla
                )
            }
        }
    }
}


@Composable
fun EliminarCuadrilla(navController: NavController, mainVM: MainVM, cuadrilla: Cuadrilla) {
    var verificacion by rememberSaveable{ mutableStateOf(false) }
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    ExtendedFloatingActionButton(
        onClick = {verificacion=true},
        icon = {
            Icon(
                painterResource(id = R.drawable.delete),
                contentDescription = null
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.abandonar, cuadrilla.nombre)
            )
        },
        containerColor =  if (isVertical) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
    )
    EstasSeguroDialog(
        show = verificacion,
        mensaje = stringResource(R.string.est_s_seguro_de_que_deseas_abandonar_la_cuadrilla),
        onDismiss = { verificacion = false }
    ) { mainVM.eliminarIntegrante(cuadrilla) ; verificacion = false ; navController.popBackStack() }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TopProfileCuadrilla(
    mainVM: MainVM,
    cuadrilla: Cuadrilla,
    pertenezco: Boolean,
    navController: NavController
){
    Row (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        CuadrillaProfileImage(cuadrilla, pertenezco, mainVM, navController = navController)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(15.dp),
        ) {
            Text(
                text = cuadrilla.descripcion,
                style = TextStyle(
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(5.dp)
            )
            Row {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location")
                Text(
                    text = cuadrilla.lugar,
                    modifier = Modifier.padding(5.dp),
                    style = TextStyle(
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CuadrillaProfileImage(
    cuadrilla: Cuadrilla,
    pertenezco: Boolean,
    mainVM: MainVM,
    navController: NavController
) {
    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf<Uri?>(Uri.parse("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png"))
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri!=null) {
            imageUri = uri
            mainVM.updateCuadrillaImage(context, cuadrilla.nombre, imageUri)
        }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        Box(Modifier.padding(16.dp)) {
            Imagen(imageUri, context, R.drawable.no_cuadrilla, 120.dp) {
                navController.navigate(
                    AppScreens.FullImageScreen.route + "/" +
                            "cuadrilla" + "/" +
                            cuadrilla.nombre
                )
            }
        }
        if(pertenezco) EditImageIcon(singlePhotoPickerLauncher = singlePhotoPickerLauncher)
    }


}



@Composable
fun Compartir(
    show:Boolean,
    accessToken: String,
    nombreCuadrilla: String,
    onDismiss: () -> Unit
){
    var showQRCode by remember { mutableStateOf(false) }
    if(show){
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {},
            title = {
                Text(text = context.getString(R.string.token_conf)) },
            text = {
                Column {
                    Text(text = context.getString(R.string.recordatorio_token))
                    Spacer(modifier = Modifier.size(10.dp))
                    Row {
                        IconButton(onClick = { openWhatsApp(accessToken, nombreCuadrilla, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.whatsapp),
                                contentDescription = null,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                        IconButton(onClick = { openTelegram(accessToken, nombreCuadrilla, context) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.telegram),
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        IconButton(onClick = { showQRCode = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.qr_code),
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp)
                            )
                        }

                    }
                }
            }
        )
        if (showQRCode) {
            val qrBitmap = generateQRCode(accessToken, 400, 400)
            if (qrBitmap != null) {
                AlertDialog(
                    onDismissRequest = { showQRCode = false },
                    title = { Text("Código QR") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Text("Tus amigos pueden escanearlo para añadirse a $nombreCuadrilla", modifier = Modifier.padding(bottom = 2.dp))
                            Image(bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showQRCode = false }) {
                            Text(text = stringResource(id = R.string.cerrar))
                        }
                    }
                )
            } else {
                Toast.makeText(context,
                    stringResource(R.string.ha_ocurrrido_un_problema_intentalo_de_nuevo), Toast.LENGTH_LONG).show()
            }
        }



    }
}

fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)
    } catch (e: IllegalArgumentException) {
        Log.d("QR CODE", "error1")
        return null
    } catch (e: WriterException) {
        Log.d("QR CODE", "error2")
        return null
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}


@Composable
fun Unirse(show:Boolean, accessToken: String, nombreCuadrilla: String, mainVM: MainVM,  onDismiss: () -> Unit, onConfirm: () -> Unit){

    var scanQRcode by remember { mutableStateOf(false) }
    val context= LocalContext.current
    val scanResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val scannedCode = data?.getStringExtra("SCAN_RESULT")
            Log.d("Codigo escaneado", scannedCode ?: "")
            if (scannedCode == accessToken){
                mainVM.agregarIntegrante(mainVM.currentUser.value!!.username, mainVM.cuadrillaMostrar.value!!.nombre)
                onDismiss() // cerrar alerts
            }
            else{
                Toast.makeText(context,
                    context.getString(R.string.c_digo_qr_incorrecto_intentalo_de_nuevo), Toast.LENGTH_LONG).show()
                onDismiss() // cerrar alerts
            }
        }
        else{
            Toast.makeText(context,
                context.getString(R.string.intentalo_de_nuevo), Toast.LENGTH_LONG).show()
        }
    }

    if(show){
        var input by rememberSaveable {mutableStateOf("")}
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { onDismiss() },
            dismissButton = {
                TextButton(onClick = { onDismiss() }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (input == accessToken){
                        onConfirm()
                    }
                    else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.incorrect_token),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(text = context.getString(R.string.unirse))
                }
            },
            title = {
                Text(text = context.getString(R.string.pregunta_unirse, nombreCuadrilla)) },
            text = {
                Column {
                    Text(text = context.getString(R.string.insert_token))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text(context.getString(R.string.token)) },
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        )
                        IconButton(onClick = { scanQRcode = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.qr_code),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(vertical = 5.dp)
                            )
                        }
                    }

                }
            }
        )

        if (scanQRcode){
            /* DEPRECATED
            val integrator = IntentIntegrator(activity).apply {
                setOrientationLocked(false)
                setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                setPrompt("Scanning Code")
            }
            scanResultLauncher.launch(integrator.createScanIntent())
             */
            val scanOptions = ScanOptions().apply {
                setBeepEnabled(false)
                setDesiredBarcodeFormats(QR_CODE)
                setPrompt("Escanea el código QR de la cuadrilla")
            }
            val scanIntent = ScanContract().createIntent(
                context,
                scanOptions
            )
            scanResultLauncher.launch(scanIntent)

        }

    }
}




@Composable
fun ListadoUsuarios(
    usuarios: List<Usuario>,
    pertenezco: Boolean,
    mainVM: MainVM,
    navController: NavController,
    numeroIntegrantes: Int
){
    var showShare by rememberSaveable { mutableStateOf(false) }
    var showJoin by rememberSaveable { mutableStateOf(false) }
    val token = mainVM.getCuadrillaAccessToken(mainVM.cuadrillaMostrar.value!!.nombre)
    Log.d("TOKEN", token)

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ){
        Text(
            text = stringResource(id = R.string.integrantes, numeroIntegrantes),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(3f)
        )
        if (pertenezco){
            FestUpButton(onClick = { showShare = true }, modifier = Modifier.weight(1f)) {
                Icon(painter = painterResource(id = R.drawable.send), "")
            }
        }
        else{
            FestUpButton(onClick = { showShare = true }, modifier = Modifier.weight(1f)) {
                Icon(painter = painterResource(id = R.drawable.join), "")
            }
        }
    }

    if (usuarios.isNotEmpty()){
        LazyRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            items(usuarios) {
                UsuarioMiniCard(usuario = it, mainVM = mainVM, navController = navController)
            }
        }
    }
    else{
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(id = R.string.no_users),
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

    }
    Compartir(showShare, token, mainVM.cuadrillaMostrar.value!!.nombre) { showShare = false }
    Unirse(showJoin, token, mainVM.cuadrillaMostrar.value!!.nombre,mainVM, onDismiss = {showJoin=false}) {
        mainVM.agregarIntegrante(mainVM.currentUser.value!!.username, mainVM.cuadrillaMostrar.value!!.nombre)
        showJoin = false
    }
}

@Composable
fun EventosCuadrilla(
    mainVM: MainVM,
    navController: NavController,
    cuadrilla: Cuadrilla
) {
    val eventos = mainVM.eventosCuadrilla(cuadrilla).collectAsState(initial = emptyList())
    val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Text(
        text = stringResource(id = R.string.eventos)+":",
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(16.dp)
    )
    if (eventos.value.isNotEmpty()) {
        if (isVertical) {
            LazyColumn {
                items(eventos.value) { evento ->
                    EventoCard(evento = evento, mainVM = mainVM, navController = navController)
                }
            }
        }
        else{
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(eventos.value) {
                    EventoMiniCard(
                        evento = it,
                        mainVM = mainVM,
                        navController = navController
                    )

                }
            }
        }
    }
    else{
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "No hay eventos",
                modifier = Modifier.padding(top = 10.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }
}