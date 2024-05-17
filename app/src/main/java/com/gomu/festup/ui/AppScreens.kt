package com.gomu.festup.ui

/**
 * Un objeto sellado, en Kotlin, es una estructura de datos que permite definir un conjunto fijo
 * y limitado de subtipos (clases, objetos o interfaces). Es un conjunto cerrado de opciones que
 * garantiza que ninguna otra clase pueda extender o implementar el conjunto definido.
 *
 * En este caso se utiliza para definir las pantallas de la aplicación
 */

sealed class AppScreens (val route: String) {
    object LoginPage: AppScreens("LoginPage")
    object App: AppScreens("App")

    object PerfilYo: AppScreens("PerfilYo")
    object PerfilUser: AppScreens("PerfilUser")
    object PerfilCuadrilla: AppScreens("PerfilCuadrilla")
    object Evento: AppScreens("Evento")

    object Ajustes: AppScreens("Ajustes")
    object EditPerfil: AppScreens("EditPerfil")

    object Feed: AppScreens("Feed")
    object Search: AppScreens("Search")
    object EventsMap: AppScreens("EventsMap")
    object EventsList: AppScreens("EventsList")

    object AddCuadrilla: AppScreens("AddCuadrilla")
    object AddEvento: AppScreens("AddEvento")

    object FullImageScreen: AppScreens("FullImageScreen")
    object SplashScreen: AppScreens("SplashScreen")

    object BuscarAmigos: AppScreens("BuscarAmigos")
    object SeguidoresSeguidosList: AppScreens("SeguidoresSeguidosList")

}