package com.example.loginapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class HomePage : AppCompatActivity() {
    private lateinit var usernameTextView: TextView

    @SuppressLint("MissingInflatedId", "SetTextI18n", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//      Start  -------> This is for fetch NAME from backend
        usernameTextView = findViewById(R.id.show_name)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val username = prefs.getString("username", "")
        if (username != null) {
            if (username.isNotEmpty()) {
                usernameTextView.text = "Welcome\n$username"
            }
        }
//      End  -------> This is for name fetch from backend
        val prefs1 = PreferenceManager.getDefaultSharedPreferences(this)
        val userid = prefs1.getString("userid", "")
        val token= prefs1.getString("accessToken","")

//--------------> Used for fetch current Location
        val setting = findViewById<LinearLayout>(R.id.setting)
        setting.setOnClickListener {
            val intent=Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }

//-------------->Calling the logOut function
        val logOut = findViewById<ImageView>(R.id.imageview11)
        logOut.setOnClickListener {
            val userToken = token.toString()
            sendLogout(this,userToken)
        }
//----------------> Calling the profile activity
        val goMessage = findViewById<ImageView>(R.id.imageview12)
        goMessage.setOnClickListener {
            Log.d(userid,"User Id Is $userid")
            val userId= userid.toString()
            val userToken = token.toString()
            if (userToken.isNotEmpty()) {
                val profileData = ProfileData(userId)

                val json = Gson().toJson(profileData)
                sendUserId(json, this, userToken)
            } else {
                Toast.makeText(this, "Token is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }
//-------------> This Function used to Retrieve the user data from data base and show it in profile page
    private fun sendUserId(json: String, context: Context,token:String) {
        // Print the JSON data before sending it to the backend
        println("Data from frontend: $json")
        println("Request Headers: $token")
        val client = OkHttpClient()
        val url = "http://192.168.0.166:8000/retrieve-user/" //-------->API url for Data retrieved
        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        println("Request URL: $url")
        println("Request Body: $json")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(context, "User id  not found", Toast.LENGTH_SHORT).show()
                }
            }
        //--------------> This function will received the response !!!!
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                println("Response Body: $responseData")//<--------- PRINTING THE RESPONSE
                runOnUiThread {
                    Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show()
            // ----------------> Send the response data to the Profile page !!!!
                    val profileResponse= Gson().fromJson(responseData,ProfileResponse::class.java)
                    val userName=profileResponse.name
                    val dob=profileResponse.birthdate
                    val number =profileResponse.phone_number
                    val gender = profileResponse.gender
                    val address = profileResponse.address
                    val state= profileResponse.state
                    val pin = profileResponse.pincode
                    val marital_sts = profileResponse.marital_status
                    storeProfileData(userName,number,gender,dob,address,state,pin,marital_sts)
                }
            //------> Checking the response is successfully received or not !!!!
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "User Id Get ", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MessagePage::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "User Id not Get", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
//-------------> This Function used to logout the application
    private fun sendLogout( context: Context,token:String) {
        println("Request Headers: $token")

        val client = OkHttpClient()
        val url = "http://192.168.0.166:8000/logout/"
     val non=""
     val body = RequestBody.create(MediaType.parse("application/json"), non)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        println("Request URL: $url")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(context, "Logout Failed !!!", Toast.LENGTH_SHORT).show()
                }
            }
            //--------------> This function will received the response !!!!
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body()?.string()
                println("Response Body: $responseData")//<--------- PRINTING THE RESPONSE
                runOnUiThread {
                    Toast.makeText(context, responseData, Toast.LENGTH_SHORT).show()
                }
//--------------> Checking the response is successfully received or not !!!!
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(context, "Logout Successful ", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@HomePage, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "Logout Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

//For store the UserId
    private data class ProfileData(
        val user_id: String
    )
//    private data class ProfileToken(
//        val token: String
//    )

//    send the response data to profile page
private fun storeProfileData(name: String, number:String, gender:String, dob:String, address:String,state:String, pin:String, marital_sts:String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val editor = prefs.edit()
    editor.putString("username", name)
    editor.putString("phone_number", number)
    editor.putString("gender", gender)
    editor.putString("birthdate", dob)
    editor.putString("address", address)
    editor.putString("state", state)
    editor.putString("pincode", pin)
    editor.putString("marital_status", marital_sts)
    editor.apply()
}

//    For store the the response of all details of profile
   private data class ProfileResponse(
       val name:String,
       val phone_number:String,
       val gender:String,
       val birthdate:String,
       val state :String,
       val pincode:String,
       val marital_status:String,
       val address:String
   )
//    private data class ResponseToken(
//        val token: String
//    )


}