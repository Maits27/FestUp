package com.gomu.festup.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gomu.festup.R
import com.gomu.festup.data.AppLanguage
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel

@Composable
fun Ajustes(
    navController: NavController,
    preferencesVM: PreferencesViewModel,
    mainVM: MainVM
) {
    val idioma by preferencesVM.idioma(mainVM.currentUser.value!!.username).collectAsState(initial = preferencesVM.currentSetLang)
    val dark by preferencesVM.darkTheme(mainVM.currentUser.value!!.username).collectAsState(initial = true)
    val receiveNotifications by preferencesVM.receiveNotifications(mainVM.currentUser.value!!.username).collectAsState(initial = true)

    var showIdiomas by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Text(
            "Visualización",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        //---------------- Idioma ----------------//
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable { showIdiomas = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.language),
                contentDescription = null,
                modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(3f)
            ) {
                Text("Idioma")
                Text(
                    text = "Castellano",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
        LanguageSelection(showIdiomas, idioma, preferencesVM) {showIdiomas = false}
        //Tema de colores
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.color),
                contentDescription = null,
                modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(3f)
            ) {
                Text("Tema de color")
                Text(
                    text = if (dark) "Dark" else "Light",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            SwitchDarkMode(preferencesVM, dark, Modifier.weight(2f))
        }
        Divider(Modifier.padding(vertical = 10.dp))
        Text(
            "Sistema",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.color),
                contentDescription = null,
                modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(3f)
            ) {
                Text("Tema de color")
                Text(
                    text = if (dark) "Dark" else "Light",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            SwitchTik(receiveNotifications, Modifier.weight(2f)){preferencesVM.changeReceiveNotifications()}
        }
    }
}

@Composable
fun SwitchTik(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheck:()->Unit
){
    // TODO: https://www.youtube.com/watch?v=Nvphdmi-6qc
    var checkedSwitch = checked
    Switch(
        modifier = modifier,
        checked = checkedSwitch,
        onCheckedChange = {
            checkedSwitch = !checkedSwitch
            onCheck()
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
@Composable
fun SwitchDarkMode(
    preferencesVM: PreferencesViewModel,
    dark: Boolean,
    modifier: Modifier = Modifier
){
    // TODO: https://www.youtube.com/watch?v=Nvphdmi-6qc
    var checkedDark = dark
    Switch(
        modifier = modifier,
        checked = checkedDark,
        onCheckedChange = {
            checkedDark = !checkedDark
            preferencesVM.changeTheme(!dark)
        },
        thumbContent = if (checkedDark) {
            {
                Icon(
                    painter = painterResource(id = R.drawable.dark),
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            {
                Icon(
                    painter = painterResource(id = R.drawable.light),
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        }
    )
}
@Composable
fun LanguageSelection(
    show: Boolean,
    idioma: AppLanguage,
    preferencesVM: PreferencesViewModel,
    onDismiss:()-> Unit
) {
    if(show){
        AlertDialog(
            onDismissRequest = {},
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
                }},
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

