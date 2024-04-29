package com.gomu.festup.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun PerfilYo(mainNavController: NavController, navController: NavController) {
}

@Preview
@Composable
fun PerfilPreview(){
    PerfilYo(mainNavController = rememberNavController(),navController = rememberNavController())
}