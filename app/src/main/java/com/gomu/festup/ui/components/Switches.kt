package com.gomu.festup.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.gomu.festup.R
import com.gomu.festup.vm.PreferencesViewModel

@Composable
fun SwitchTik(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheck:()->Unit
){
    var checkedSwitch by remember { mutableStateOf(checked) }

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