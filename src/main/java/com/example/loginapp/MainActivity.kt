package com.example.loginapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //@SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_xml)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//----------------> GO TO REGISTER PAGE
        val goRegister =findViewById<TextView>(R.id.signup)
        goRegister.setOnClickListener {
            val intent = Intent(this,RegisterPage::class.java)
            startActivity(intent)
        }

//----------------> FOR LOGIN WORK
        val mail = findViewById<TextInputEditText>(R.id.user_mail)
        val password = findViewById<TextInputEditText>(R.id.password)
        val login =findViewById<Button>(R.id.loginButton)

        login.setOnClickListener {
            val email = mail.text.toString().trim()
            val pass = password.text.toString().trim()
            when {
                email.isEmpty()-> mail.error = "Enter a valid email address."
                pass.isEmpty() -> password.error =
                    "Please enter a strong password (at least 8 characters, both upper and lower case, and at least 1 digit)"
                else -> {
                    val loginData = LoginData(email, pass)
                    val json = Gson().toJson(loginData)
                    sendDataToBackend(json, this)
                }
            }
        }
    }
    private fun sendDataToBackend(json: String, context: Context) {
        // Print the JSON data before sending it to the backend
        println("Data from frontend: $json")

        val client = OkHttpClient()
        val url = "http://192.168.0.166:8000/api/login/"
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
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                println("Response Body: $responseData")//PRINTING THE RESPONSE
                runOnUiThread {
                    Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show()

// ----------------> send the response data to the welcome page or home page
                    val loginResponse= Gson().fromJson(responseData, LoginResponse::class.java)
                    val userName=loginResponse.name
                    storeUsername(userName)

                    val useridResponse= Gson().fromJson(responseData, LoginResponse::class.java)
                    val userId=useridResponse.user_id
                    val user_token= loginResponse. refresh
                    val access_token =loginResponse.access
                    storeUserId(userId,user_token,access_token)



                }
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, HomePage::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

// This data class for hold the login id and password
    private data class LoginData(
        val email: String,
        val password: String
    )

// This data class for hold the response of login
    private data class LoginResponse(
        val refresh: String,
        val access:String,
        val name: String,
        val user_id:String,

    )
    // ----------------> The response data converting JSON to string
    private fun storeUsername(name: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString("username", name)
        editor.apply()
    }

    private fun storeUserId(userid: String,user_token:String,access_token: String) {
        val prefs1 = PreferenceManager.getDefaultSharedPreferences(this)
        val editor1 = prefs1.edit()
        editor1.putString("userid", userid)
        editor1.putString("token", user_token)
        editor1.putString("accessToken", access_token)
        editor1.apply()
    }

}