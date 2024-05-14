package com.gomu.festup.ui.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gomu.festup.LocalDatabase.Entities.Usuario
import com.gomu.festup.R
import com.gomu.festup.ui.AppScreens
import com.gomu.festup.vm.MainVM
import java.time.Instant
import java.util.Date

@Composable
fun UsuarioMiniCard(
    mainVM: MainVM,
    navController: NavController,
    usuario: Usuario
) {
    val onCardClick: () -> Unit = {
        mainVM.usuarioMostrar.add(usuario)
        if (mainVM.currentUser.value == mainVM.usuarioMostrar.last()){
            navController.navigate(AppScreens.PerfilYo.route)
        }
        else{
            navController.navigate(AppScreens.PerfilUser.route)
        }
    }

    val imageUri by remember {
        mutableStateOf("http://34.16.74.167/userProfileImages/${usuario.username}.png")
    }

    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable { onCardClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.DISABLED)  // Para que no la guarde en caché-RAM
                .diskCachePolicy(CachePolicy.DISABLED)    // Para que no la guarde en caché-disco
                .build(),
            contentDescription = stringResource(id = R.string.user_image),
            error = painterResource(id = R.drawable.no_user),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .border(2.dp, color = MaterialTheme.colorScheme.primary, CircleShape)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = usuario.username, fontSize = 10.sp)
    }
}