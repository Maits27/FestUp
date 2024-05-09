package com.gomu.festup.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Cuadrilla
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.ui.components.dialogs.EstasSeguroDialog
import com.gomu.festup.vm.MainVM

@Composable
fun CuadrillaCard(
    cuadrilla: Cuadrilla,
    mainVM: MainVM,
    navController: NavController,
    isRemoveAvailable: Boolean,
) {

    val onCardClick: (Cuadrilla) -> Unit = {
        mainVM.cuadrillaMostrar.value = cuadrilla
        navController.navigate(AppScreens.PerfilCuadrilla.route)
    }

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clickable { onCardClick(cuadrilla) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Cuadrilla profile image",
                onError = {
                    imageUri = "http://34.16.74.167/cuadrillaProfileImages/no-cuadrilla.png"
                },
                modifier = Modifier.size(50.dp)
            )
            Column(
                Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = cuadrilla.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = cuadrilla.lugar,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isRemoveAvailable) {
                var verificacion by rememberSaveable { mutableStateOf(false) }
                Button(
                    onClick = { verificacion = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        "",
                    )
                }
                EstasSeguroDialog(
                    show = verificacion,
                    mensaje = "Si eliminas esta cuadrilla tendrás que volver a solicitar entrar.",
                    onDismiss = { verificacion = false },
                    onConfirm = { mainVM.eliminarIntegrante(cuadrilla); verificacion = false }
                )
            }
        }
    }
}

@Composable
fun CuadrillaCardParaEventosAlert(
    cuadrilla: Cuadrilla,
    apuntado: Boolean,
    mainVM: MainVM,
    onClick: () -> Unit,
) {

    var imageUri by remember {
        mutableStateOf("http://34.16.74.167/cuadrillaProfileImages/${cuadrilla.nombre}.png")
    }

    var checkedSwitch by remember { mutableStateOf(apuntado) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Cuadrilla profile image",
                placeholder = painterResource(id = R.drawable.no_cuadrilla),
                onError = {
                    imageUri = "http://34.16.74.167/cuadrillaProfileImages/no-cuadrilla.png"
                },
                modifier = Modifier.size(50.dp).clip(CircleShape)
            )
            Column(
                Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = cuadrilla.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = cuadrilla.lugar,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = checkedSwitch,
                onCheckedChange = {
                    checkedSwitch = !checkedSwitch
                    if (checkedSwitch){
                        mainVM.apuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                    }
                    else{
                        mainVM.desapuntarse(cuadrilla, mainVM.eventoMostrar.value!!)
                    }
                },
                thumbContent = if (checkedSwitch) {
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}