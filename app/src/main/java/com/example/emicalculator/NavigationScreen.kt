package com.example.emicalculator

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = DestinationScreen.SplashScreenDest.route
    ) {
        composable(route = DestinationScreen.SplashScreenDest.route) {
            SplashScreen(navController = navController)
        }

        composable(route = DestinationScreen.LoginScreenDest.route) {
            LoginScreen( navController = navController)
        }
        composable(route = DestinationScreen.SignUpScreenDest.route) {
            SignUp( navController = navController)
        }
        composable(route = DestinationScreen.SignUpScreenDest.route) {
            SignUp( navController = navController)
        }

        composable(route = DestinationScreen.MainScreenDest.route) {
            MainScreen(navController = navController)
        }



    }
}