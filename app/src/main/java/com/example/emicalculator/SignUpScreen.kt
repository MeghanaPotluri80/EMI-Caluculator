package com.example.emicalculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SignUp(navController: NavHostController) {
    val authHelper = FirebaseAuth.getInstance()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val isLoggedIn by remember { derivedStateOf { authHelper.getCurrentUser() != null } }
    val focusManager = LocalFocusManager.current





    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                Modifier.size(150.dp)
            )


        Text(
            text = "Welcome Back",
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFFF9800)
        )

        Text(
            text = "Continue to Sign Up !",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF3F51B5)
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true


        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = mobileNo,
            onValueChange = { mobileNo = it },
            label = { Text("Mobile No.") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth(),

            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),

            singleLine = true
        )

        Button(
            onClick = {
                if (fullName.isNotEmpty() && email.isNotEmpty() && mobileNo.isNotEmpty() && password.isNotEmpty()) {
                    isError = false
                    runBlocking {
                        try {
                            authHelper.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val user = authHelper.currentUser
                                        val database = Firebase.database
                                        val myRef =
                                            database.getReference("Users").child(user!!.uid)

                                        val userDataModel = UserDataModel(
                                            fullName,
                                            email,
                                            mobileNo,
                                            password
                                        )
                                        myRef.setValue(userDataModel).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                navController.navigate(DestinationScreen.MainScreenDest.route)
                                            }
                                            else{
                                                navController.navigate(DestinationScreen.LoginScreenDest.route)
                                            }


                                        }

                                    } else {
                                        isError = true
                                        errorMessage = (it.exception?.message
                                            ?: "Singed Up Failed! ")
                                    }
                                }

                        } catch (e: Exception) {

                            isError = true
                            errorMessage = e.message.toString();
                        }
                    }
//                    onLoginClick(email, password)
//                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                } else {
                    isError = true
                    errorMessage = "Please fill all fields."
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "SignUp")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Have an account ? Login", modifier = Modifier.clickable {
            navController.navigate(DestinationScreen.LoginScreenDest.route)

        })


    }
}

data class UserDataModel(
    var fullName: String = "",
    var email: String = "",
    var mobileNo: String = "",
    var password: String = "",
)