package com.example.nyumba10

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var edtemail: EditText
    lateinit var edtpassword:EditText
    lateinit var btnlogin: Button
    lateinit var tvRegister: TextView
    lateinit var tvReset:TextView
    lateinit var progress: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtemail = findViewById(R.id.mEdtEmail)
        edtpassword = findViewById(R.id.mEdtPassword)
        btnlogin = findViewById(R.id.mBtnLogin)
        tvRegister = findViewById(R.id.mTvLogin)
        tvReset = findViewById(R.id.mTvReset)
        mAuth = FirebaseAuth.getInstance()
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")

        btnlogin.setOnClickListener {
            var email = edtemail.text.toString().trim()
            var password = edtpassword.text.toString().trim()

            if (email.isEmpty()){
                edtemail.setError("Please fill this input")
                edtemail.requestFocus()
            }else if(password.isEmpty()){
                edtpassword.setError("Please fill the input")
                edtpassword.requestFocus()
            }else{
                // Proceed to register  the user
                progress.show()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    progress.dismiss()
                    if (it.isSuccessful){
                        Toast.makeText(this, "Registrationn successful",
                            Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    }else{
                        displaymessage("Error", it.exception!!.message.toString())
                    }
                }
            }


        }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        tvReset.setOnClickListener {

        }
    }


    fun displaymessage(title:String, message:String){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Ok",null)
        alertDialog.create().show()
    }
}