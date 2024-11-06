package com.example.otpvertification

import OtpVerificationScreen
import PhoneNumberEntryScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val context = LocalContext.current
            NavHost(
                navController = navController, startDestination = "PhoneNumberEntryScreen"
            ){
                composable("PhoneNumberEntryScreen") {
                    PhoneNumberEntryScreen(this@MainActivity,navController)
                }
                composable("verificationScreen") {
                    OtpVerificationScreen()
                }
            }
        }
    }
}
