package com.gomu.festup.ui.components.dialogs

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
import androidx.compose.ui.unit.dp
import com.gomu.festup.R
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.vm.PreferencesViewModel


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
                    Text(text = "Cancelar")
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
                    Text(text = "Selecciona tu idioma")
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
                                preferencesVM.changeLang(AppLanguage.ES)
                                onDismiss()
                            }
                        )
                        Text("Castellano")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = idioma.code == "eu",
                            onClick = {
                                preferencesVM.changeLang(AppLanguage.EU)
                                onDismiss()
                            }
                        )
                        Text("Euskera")
                    }
                }
            }
        )
    }
}

