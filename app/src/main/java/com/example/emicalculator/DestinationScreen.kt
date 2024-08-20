package com.example.emicalculator

sealed class DestinationScreen(val route: String) {
    object SplashScreenDest : DestinationScreen(route = "splash_screen")
    object MainScreenDest : DestinationScreen(route = "main_screen")
    object LoginScreenDest : DestinationScreen(route = "login_screen")
    object SignUpScreenDest : DestinationScreen(route = "sign_up_screen")
}