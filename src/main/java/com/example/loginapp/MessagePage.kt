package com.example.loginapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
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
import java.util.Calendar

class MessagePage : AppCompatActivity() {

//    private var rootLayout: View? = null
//    private var scrollView: ScrollView? = null

    private lateinit var usernameText: TextInputEditText
    private lateinit var userNumber: TextInputEditText
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

//--------> For Calender to pick a date
//        userDob.setOnClickListener { showDatePickerDialog() }

//--------> Back Arrow to Home page
        val goHome=findViewById<ImageView>(R.id.profileArrow)
        goHome.setOnClickListener {
            val back=Intent(this,HomePage::class.java)
            startActivity(back)
        }


        val prefs1 = PreferenceManager.getDefaultSharedPreferences(this)
        val token= prefs1.getString("token","")
        val id =prefs1.getString("userid","")
        println("token:$token")

        usernameText = findViewById(R.id.name_user)
        userNumber= findViewById(R.id.num_user)
        userGender= findViewById(R.id.user_gender)
        userDob= findViewById(R.id.user_birth)
        userState= findViewById(R.id.user_state)
        userPinCode = findViewById(R.id.user_pincode)
        userMaritalStatus = findViewById(R.id.user_marital_status)
        userAddress = findViewById(R.id.user_address)
        userUpdate = findViewById(R.id.update)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val username = prefs.getString("username", "")
        val phone_number= prefs.getString("phone_number","")
        val gender= prefs.getString("gender","")
        val birth= prefs.getString("birthdate","")
        val address= prefs.getString("address","")
        val user_state =prefs.getString("state","")
        val user_pin = prefs.getString("pincode","")
        val mStatus = prefs.getString("marital_status","")
            println("data:  $phone_number    &  $birth")
            usernameText.setText("$username")
            userNumber.setText("$phone_number")
            userState.setText("$address")
            userDob.setText("$birth")
            userGender.setText("$gender")
            userState.setText("$user_state")
            userPinCode.setText("$user_pin")
            userMaritalStatus.setText("$mStatus")

        userUpdate.setOnClickListener {
            val name = usernameText.text.toString().trim()
            val number = userNumber.text.toString().trim()
            val user_gender = userGender.text.toString().trim()
            val birth_date = userDob.text.toString().trim()
            val state = userState.text.toString().trim()
            val pinCode =userPinCode.text.toString().trim()
            val maritalStatus = userMaritalStatus.text.toString().trim()
            val add = userAddress.text.toString().trim()
            val user_token=token.toString()
            val id=id.toString()
            when {
                name.isEmpty()-> usernameText.error = "Enter your Name."
                number.isEmpty() -> userNumber.error = "Enter your Number."
                user_gender.isEmpty() -> userGender.error = "Enter your Gender."
                birth_date.isEmpty() -> userDob.error = "Enter your Date of Birth."
                state.isEmpty() -> userState.error = "Enter your State."
                pinCode.isEmpty() -> userPinCode.error = "Enter your Pin code."
                maritalStatus.isEmpty() -> userMaritalStatus.error = "Enter 'Married' or 'Unmarried'."
                add.isEmpty() -> userAddress.error = "Enter your Address."
                else -> {
                    val newData = ProfileUpdateData(id,name,number,user_gender,birth_date,state,pinCode,maritalStatus,add)
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
        println("token from frontend: $userToken")

        val client = OkHttpClient()
        val url = "http://192.168.0.166:8000/update-user/"

        val body = RequestBody.create(MediaType.parse("application/json"), json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $userToken")
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
        val user_id:String,
        val name:String,
        val phone_number:String,
        val gender:String,
        val birthdate:String,
        val state :String,
        val pincode:String,
        val marital_status:String,
        val address:String
    )
    
//    private fun getScrollY(view: View): Int {
//        val scrollView = view.parent as? ScrollView
//        return if (scrollView == null) {
//            view.scrollY
//        } else {
//            view.scrollY + scrollView.scrollY
//        }
//    }
//    private fun showDatePickerDialog() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(this,
//            { _, selectedYear, selectedMonth, selectedDay ->
//                // Month is 0 based, so you have to add 1 to display it correctly
//                val dateOfBirth = "$selectedYear/${selectedMonth + 1}/$selectedDay"
//                userDob.setText(dateOfBirth)
//            },
//            year, month, day
//        )
//        datePickerDialog.show()
//    }
}