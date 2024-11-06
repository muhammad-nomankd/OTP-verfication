import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.otpvertification.GenerateOtpViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PhoneNumberEntryScreen(activity: ComponentActivity, navController: NavController) {
    var country by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    var countryCode by remember { mutableStateOf<String?>("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val coroutinesScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "OTP Verification", color = Color.Black, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(modifier = Modifier.fillMaxWidth(),
            value = country,
            onValueChange = { country = it },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            label = { Text("Country") })
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            singleLine = true,
            label = { Text("Phone Number") },
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (country.isEmpty()) {
                    Toast.makeText(context, "Please select country.", Toast.LENGTH_LONG).show()
                    return@Button
                }
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(context, "Enter phone number", Toast.LENGTH_LONG).show()
                    return@Button
                }

                countryCode = getCountryCodeByCountryName(country)
                isLoading = true
                coroutinesScope.launch {
                    Toast.makeText(
                        context,
                        "OTP sent to ${getCountryCodeByCountryName(country) + phoneNumber}",
                        Toast.LENGTH_LONG
                    ).show()
                    delay(1000)
                    isLoading = false
                    navController.navigate("verificationScreen")

                }

                countryCode?.let {
                    GenerateOtpViewModel().generateOtp(activity, it, phoneNumber)
                }
            }, modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Send OTP")
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 1.dp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                color = Color.Gray
            )
        }
    }
}

fun getCountryCodeByCountryName(countryName: String): String? {
    val phoneUtil = PhoneNumberUtil.getInstance()
    for (locale in Locale.getAvailableLocales()) {
        if (locale.displayCountry.equals(countryName, ignoreCase = true)) {
            val isoCode = locale.country
            val countryCode = phoneUtil.getCountryCodeForRegion(isoCode)
            if (countryCode != 0) {
                return "+$countryCode"

            }
        }
    }
    return ""
}