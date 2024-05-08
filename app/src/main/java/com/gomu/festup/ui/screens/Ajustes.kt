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
import com.gomu.festup.ui.components.SwitchDarkMode
import com.gomu.festup.ui.components.SwitchTik
import com.gomu.festup.ui.components.dialogs.LanguageSelection
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel

@Composable
fun Ajustes(
    preferencesVM: PreferencesViewModel,
    mainVM: MainVM,
    idioma: AppLanguage,
    dark: Boolean,
    receiveNotifications: Boolean
) {
    var showIdiomas by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ){
        Text(
            "Visualización:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        //---------------- Idioma ----------------//
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { showIdiomas = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.language),
                contentDescription = null,
                modifier = Modifier.padding(10.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Idioma")
                Text(
                    text = if (idioma.code == "es") "Castellano" else "Euskera",
                    style = MaterialTheme.typography.bodySmall
                )
            }

        }
        LanguageSelection(showIdiomas, idioma, preferencesVM) {showIdiomas = false}
        //Tema de colores
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
            "Sistema:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                painter = painterResource(id = R.drawable.notifications_active),
                contentDescription = null,
                modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(3f)
            ) {
                Text("Notificaciones")
                Text(
                    text = if (receiveNotifications) "Recibir" else "No recibir",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            SwitchTik(preferencesVM, mainVM, receiveNotifications, Modifier.weight(2f))
        }
    }
}

