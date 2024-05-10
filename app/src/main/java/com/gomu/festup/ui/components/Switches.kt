package com.gomu.festup.ui.components

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.datastore.preferences.preferencesDataStore
import com.gomu.festup.R
import com.gomu.festup.vm.MainVM
import com.gomu.festup.vm.PreferencesViewModel

@Composable
fun SwitchTik(
    preferencesVM: PreferencesViewModel,
    mainVM: MainVM,
    checked: Boolean,
    modifier: Modifier = Modifier
){
    var seguidos = mainVM.listaSeguidos(mainVM.currentUser.value!!).collectAsState(initial = emptyList())
    var checkedSwitch by remember { mutableStateOf(checked) }

    Switch(
        modifier = modifier,
        checked = checkedSwitch,
        onCheckedChange = {
            checkedSwitch = !checkedSwitch
            preferencesVM.changeReceiveNotifications()
            if (checkedSwitch){
                mainVM.subscribeUser()
                mainVM.suscribirASeguidos(seguidos.value)
            }else{
                mainVM.unSubscribeUser()
                mainVM.unSuscribeASeguidos(seguidos.value)
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
@Composable
fun SwitchDarkMode(
    preferencesVM: PreferencesViewModel,
    dark: Boolean,
    modifier: Modifier = Modifier
){
    // TODO: https://www.youtube.com/watch?v=Nvphdmi-6qc
    var checkedDark by remember { mutableStateOf(dark) }

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