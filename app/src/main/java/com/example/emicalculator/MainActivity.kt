
package com.example.emicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.emicalculator.ui.theme.EMICalculatorTheme
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EMICalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    MainScreen()
                    NavigationScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var selectedLoanType by remember { mutableStateOf<LoanType?>(null) }


    Column {
        NewsHeader(navController)
        Spacer(modifier = Modifier.padding(10.dp))
    if (selectedLoanType == null) {
        LoanTypeSelectionScreen { selectedLoanType = it }
    } else {
        LoanDetailsScreen(selectedLoanType!!) { selectedLoanType = null }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsHeader(navController: NavHostController) {
    TopAppBar(
        title = {
            Text(text = "EMI Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            IconButton(onClick = {/* Do Something*/ }) {
//                Icon(Icons.Filled.Home, null)
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    Modifier.size(24.dp)
                )
            }
        },
        actions = {
            Button(
                onClick = {
                    val authHelper = FirebaseAuth.getInstance()
                    authHelper.signOut()
                    navController.navigate(DestinationScreen.LoginScreenDest.route){
                        popUpTo(route = DestinationScreen.MainScreenDest.route) {
                            inclusive = true
                        }
                    }



                }, colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(text = "Logout", color = Color.White)
            }
        },
//        backgroundColor = Color(0xFF3F51B5)
    )
}

@Composable
fun LoanTypeSelectionScreen(onLoanTypeSelected: (LoanType) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        
        Text("Select Loan Type", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        LoanType.values().forEach { loanType ->
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onLoanTypeSelected(loanType) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(loanType.displayName, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(loanType.description)
                }
            }
        }
    }
}

@Composable
fun LoanDetailsScreen(loanType: LoanType, onBack: () -> Unit) {
    var principal by remember { mutableStateOf("") }
    var rateOfInterest by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }
    var gracePeriod by remember { mutableStateOf("") }
    var initialFee by remember { mutableStateOf("") }
    var emiResult by remember { mutableStateOf("") }
    var interestPayable by remember { mutableStateOf("") }
    var totalPayment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Loan Details - ${loanType.displayName}", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = principal,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = { principal = it },
            label = { Text("Principal Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rateOfInterest,
            onValueChange = { rateOfInterest = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text("Rate of Interest (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tenure,
            onValueChange = { tenure = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = { Text("Tenure (Years)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (loanType == LoanType.ANNUITY) {
            OutlinedTextField(
                value = gracePeriod,
                onValueChange = { gracePeriod = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Grace Period (Months) - Optional") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = initialFee,
                onValueChange = { initialFee = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text("Initial Fee (%) - Optional") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val emiCalculation = calculateEmi(principal, rateOfInterest, tenure, gracePeriod, initialFee, loanType)
                emiResult = emiCalculation.first
                interestPayable = emiCalculation.second
                totalPayment = emiCalculation.third
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate EMI")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(emiResult, fontSize = 24.sp, color = Color.Red, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Principal Amount", fontWeight = FontWeight.Bold)
                Text("₹$principal", fontSize = 20.sp)
            }
            Column {
                Text("Interest Payable", fontWeight = FontWeight.Bold)
                Text("₹$interestPayable", fontSize = 20.sp)
            }
            Column {
                Text("Total Payment", fontWeight = FontWeight.Bold)
                Text("₹$totalPayment", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onBack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Loan Type Selection")
        }
    }
}

fun calculateEmi(
    principal: String,
    rateOfInterest: String,
    tenure: String,
    gracePeriod: String,
    initialFee: String,
    loanType: LoanType
): Triple<String, String, String> {
    val p = principal.toDoubleOrNull() ?: return Triple("Invalid Principal Amount", "0", "0")
    val r = (rateOfInterest.toDoubleOrNull() ?: return Triple("Invalid Rate of Interest", "0", "0")) / 12 / 100
    val n = (tenure.toDoubleOrNull() ?: return Triple("Invalid Tenure", "0", "0")) * 12

    val emi = (p * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
    val totalPayable = emi * n
    val interestPayable = totalPayable - p

    return Triple(
        "EMI: ₹%.2f".format(emi),
        "%.2f".format(interestPayable),
        "%.2f".format(totalPayable)
    )
}

enum class LoanType(val displayName: String, val description: String) {
    DIFFERENTIAL("Differential", "The loan amount is evenly divided for the entire term, and interest is charged monthly on the remaining amount of money."),
    ANNUITY("Annuity", "The loan is repaid in fixed monthly installments, which include both principal and interest components.")
}

@Preview
@Composable
private fun TestPreview() {

//    MainScreen(navController)
}