package com.gomu.festup.ui

sealed class AppScreens (val route: String) {
    object LoginPage: AppScreens("LoginPage")
    object App: AppScreens("App")

    object PerfilYo: AppScreens("PerfilYo")
    object PerfilUser: AppScreens("PerfilUser")
    object PerfilCuadrilla: AppScreens("PerfilCuadrilla")
    object SeguidoresSeguidosList: AppScreens("SeguidoresSeguidosList")

    object Ajustes: AppScreens("Ajustes")
    object EditPerfil: AppScreens("EditPerfil")

    object Feed: AppScreens("Feed")
    object Search: AppScreens("Search")
    object EventsMap: AppScreens("EventsMap")
    object EventsList: AppScreens("EventsList")

    object Evento: AppScreens("Evento")

    object AddCuadrilla: AppScreens("AddCuadrilla")
    object AddEvento: AppScreens("AddEvento")

    object FullImageScreen: AppScreens("FullImageScreen")

    object SplashScreen: AppScreens("SplashScreen")

    object BuscarAmigos: AppScreens("BuscarAmigos")

}