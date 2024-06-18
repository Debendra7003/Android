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

class MessagePage : AppCompatActivity() {

    private lateinit var usernameText: TextInputEditText
    private lateinit var userNumberText: TextInputEditText
    private lateinit var userState: TextInputEditText
    private lateinit var userDob: TextInputEditText
    private lateinit var userGender: TextInputEditText
    private lateinit var userPinCode: TextInputEditText
    private lateinit var userMaritalStatus: TextInputEditText
    private lateinit var userAddress: TextInputEditText
    private lateinit var userUpdate: Button

    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs1 = PreferenceManager.getDefaultSharedPreferences(this)
        val token= prefs1.getString("token","")

        usernameText = findViewById(R.id.name_user)
        userNumberText= findViewById(R.id.number_user)
        userGender= findViewById(R.id.user_gender)
        userDob= findViewById(R.id.user_dob)
        userState= findViewById(R.id.user_state)
        userPinCode = findViewById(R.id.user_pincode)
        userMaritalStatus = findViewById(R.id.user_marital_status)
        userAddress = findViewById(R.id.user_address)
        userUpdate = findViewById(R.id.update)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val username = prefs.getString("username", "")
        val birthdate= prefs.getString("birthdate","")
        val phone= prefs.getString("phone","")
        val gender= prefs.getString("gender","")
        val address= prefs.getString("address","")
        val user_state =prefs.getString("state","")
        val user_pin = prefs.getString("pincode","")
        val mStatus = prefs.getString("marital_status","")

            usernameText.setText("$username")
            userNumberText.setText("$phone")
            userState.setText("$address")
            userDob.setText("$birthdate")
            userGender.setText("$gender")
            userState.setText("$user_state")
            userPinCode.setText("$user_pin")
            userMaritalStatus.setText("$mStatus")

        userUpdate.setOnClickListener {
            val name = usernameText.text.toString().trim()
            val number = userNumberText.text.toString().trim()
            val user_gender = userGender.text.toString().trim()
            val birth_date = userDob.text.toString().trim()
            val state = userState.text.toString().trim()
            val pinCode =userPinCode.text.toString().trim()
            val maritalStatus = userMaritalStatus.text.toString().trim()
            val add = userAddress.text.toString().trim()
            val user_token=token.toString()
            when {
                name.isEmpty()-> usernameText.error = "Enter your Name."
                number.isEmpty() -> userNumberText.error = "Enter your Number."
                user_gender.isEmpty() -> userGender.error = "Enter your Gender."
                birth_date.isEmpty() -> userDob.error = "Enter your Date of Birth."
                state.isEmpty() -> userState.error = "Enter your State."
                pinCode.isEmpty() -> userPinCode.error = "Enter your Pin code."
                maritalStatus.isEmpty() -> userMaritalStatus.error = "Enter 'Married' or 'Unmarried'."
                add.isEmpty() -> userAddress.error = "Enter your Address."
                else -> {
                    val newData = ProfileUpdateData(name,birth_date,number,user_gender,state,pinCode,maritalStatus,add)
                    val json = Gson().toJson(newData)
                    sendUpdateData(json, this,user_token)
                }
            }
        }
    }
//hii hello good morning Deb
    private fun sendUpdateData(json: String, context: Context,userToken:String) {
        // Print the JSON data before sending it to the backend
        println("Data from frontend: $json")

        val client = OkHttpClient()
        val url = "http://192.168.0.166/myapp/login/"

        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .patch(body)
            .addHeader("Authorization", "token $userToken") 
            .build()

        println("Request URL: $url")
        println("Request Body: $json")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(context, "Data Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                println("Response Body: $responseData")//PRINTING THE RESPONSE
                runOnUiThread {
                    Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show()
                }
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "Data Update Successful", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "Data Update Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private data class ProfileUpdateData(
        val name:String,
        val birthdate:String,
        val phone:String,
        val gender:String,
        val state:String,
        val pincode:String,
        val marital_status:String,
        val address:String
    )
}