package com.example.loginapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import android.content.Context
import android.preference.PreferenceManager

class RegisterPage : AppCompatActivity() {

//    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //FOR BACK ARROW
        val goHome =findViewById<TextView>(R.id.signup)
        goHome.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val name = findViewById<TextInputEditText>(R.id.userName)
        val number = findViewById<TextInputEditText>(R.id.number)
        val mail = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val register =findViewById<Button>(R.id.registerButton)

        register.setOnClickListener {
            val userName = name.text.toString().trim()
            val mobileNumber = number.text.toString().trim()
            val email = mail.text.toString().trim()
            val pass = password.text.toString().trim()

            when {
                userName.isEmpty() -> name.error = "Please enter Full Name!"
                mobileNumber.isEmpty() -> number.error = "Enter a valid phone number."
                email.isEmpty()-> mail.error = "Enter a valid email address."
                pass.isEmpty() -> password.error =
                    "Please enter a strong password (at least 8 characters, both upper and lower case, and at least 1 digit)"
                else -> {
                    val registrationData = RegistrationData(mobileNumber,userName,email, pass)
                    val json = Gson().toJson(registrationData)
                    sendDataToBackend(json, this)
                }
            }
        }
    }
    private fun sendDataToBackend(json: String, context: Context) {
        // Print the JSON data before sending it to the backend
        println("Data from frontend: $json")

        val client = OkHttpClient()
        val url = "http://192.168.0.166:8000/register/"

        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        println("Request URL: $url")
        println("Request Body: $json")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()

                println("Response Body: $responseData")

                runOnUiThread {

                    Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show()
                }
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
    private data class RegistrationData(
        val phone_number:String,
        val name: String,
        val email: String,
        val password: String
    )
}