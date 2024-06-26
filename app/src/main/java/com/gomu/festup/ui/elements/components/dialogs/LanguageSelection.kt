package com.gomu.festup.ui.elements.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gomu.festup.R
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.ui.vm.PreferencesViewModel

// Dialogo para mostrar al usuario los diferentes idiomas que tiene disponibles la aplicacion
// Se despliega cuando el usuario accede a los ajustes del idioma desde las preferencias de su perfil
@Composable
fun LanguageSelection(
    show: Boolean,
    idioma: AppLanguage,
    preferencesVM: PreferencesViewModel,
    onDismiss:()-> Unit
) {
    if(show){
        AlertDialog(
            onDismissRequest = {onDismiss()},
            confirmButton = {
                TextButton(onClick = { onDismiss()}) {
                    Text(text = stringResource(id = R.string.aceptar))
                }
            },
            title = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.language),
                        contentDescription = null)
                    Text(text = stringResource(id = R.string.insert_idioma))
                }
            },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = idioma.code == "es",
                            onClick = {
                                preferencesVM.changeLang(AppLanguage.getFromCode("es"))
                            }
                        )
                        Text("Castellano")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = idioma.code == "eu",
                            onClick = {
                                preferencesVM.changeLang(AppLanguage.getFromCode("eu"))
                            }
                        )
                        Text("Euskera")
                    }
                }
            }
        )
    }
}

