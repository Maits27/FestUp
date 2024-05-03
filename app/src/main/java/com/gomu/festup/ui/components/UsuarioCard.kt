package com.gomu.festup.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM

@Composable
fun UsuarioCard(
    usuario: Usuario,
    mainVM: MainVM,
    navController: NavController
) {

    val onCardClick: (Usuario) -> Unit = {
        mainVM.usuarioMostrar.value = usuario
        navController.navigate(AppScreens.PerfilUser.route)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick(usuario) }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            AsyncImage(
                // TODO esto será usuario.profileImagePath
                model = "http://34.16.74.167/userProfileImages/no-user.png",
                contentDescription = "User image",
                placeholder = painterResource(id = R.drawable.party),
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.size(15.dp))
            Column {
                Text(
                    text = usuario.username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}