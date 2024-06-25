package com.example.loginapp

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OtpVerification : AppCompatActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var repasswordEditText: TextInputEditText
    private lateinit var repasswordLayout: TextInputLayout
    private lateinit var otpEditText: TextInputEditText
    private lateinit var otpLayout: TextInputLayout
    private lateinit var updateButton: Button

    private var isPasswordVisible: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otplayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize the views
        passwordLayout = findViewById(R.id.password)
        passwordEditText = findViewById(R.id.password_user)
        repasswordLayout = findViewById(R.id.repassword)
        repasswordEditText = findViewById(R.id.repassword_user)
        otpLayout = findViewById(R.id.otp)
        otpEditText = findViewById(R.id.otp_user)
        scrollView = findViewById(R.id.scrollview)
        updateButton = findViewById(R.id.pass_update)
        scrollView = findViewById(R.id.scrollview)

        // Set input types
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        repasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Toggle password visibility listeners
        passwordLayout.setEndIconOnClickListener {
            togglePasswordVisibility(passwordEditText)
        }
        repasswordLayout.setEndIconOnClickListener {
            togglePasswordVisibility(repasswordEditText)
        }

        // Text change listeners for password and re-password fields
        passwordEditText.addTextChangedListener(passwordTextWatcher)
        repasswordEditText.addTextChangedListener(repasswordTextWatcher)

        // Keyboard visibility listener
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is open
                scrollView.scrollTo(0, scrollView.bottom)
            } else {
                // Keyboard is closed
                scrollView.scrollTo(0, scrollView.top)
            }
        }
    }
    private val passwordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val password = s.toString()
            if (isPasswordStrong(password)) {
                passwordLayout.helperText = "Strong password"
                passwordLayout.setHelperTextColor(resources.getColorStateList(R.color.colorStrong))
            } else if (password.length >= 8) {
                passwordLayout.helperText = "Medium strength"
                passwordLayout.setHelperTextColor(resources.getColorStateList(R.color.colorError))
            } else {
                passwordLayout.helperText = "Weak password"
                passwordLayout.setHelperTextColor(resources.getColorStateList(R.color.colorWeak))
            }
            checkPasswordsMatch()
            checkAllFieldsFilled()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val repasswordTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            checkPasswordsMatch()
            checkAllFieldsFilled()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun togglePasswordVisibility(editText: TextInputEditText) {
        isPasswordVisible = !isPasswordVisible
        editText.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text?.length ?: 0)
    }

    private fun isPasswordStrong(password: String): Boolean {
        val lengthRequirement = password.length >= 8
        val digitRequirement = password.any { it.isDigit() }
        val letterRequirement = password.any { it.isLetter() }
        return lengthRequirement && digitRequirement && letterRequirement
    }

    private fun checkPasswordsMatch() {
        val password = passwordEditText.text.toString()
        val repassword = repasswordEditText.text.toString()

        if (password == repassword) {
            repasswordLayout.error = null
        } else {
            repasswordLayout.error = "Passwords do not match"
        }
        checkAllFieldsFilled()
    }

    private fun checkAllFieldsFilled() {
        val otp = otpEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val repassword = repasswordEditText.text.toString().trim()

        if (otp.isNotEmpty() && password.isNotEmpty() && repassword.isNotEmpty() && password == repassword) {
            updateButton.isEnabled = true
        } else {
            updateButton.isEnabled = false
        }
    }
}