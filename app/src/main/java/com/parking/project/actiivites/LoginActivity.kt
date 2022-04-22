package com.parking.project.actiivites

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.parking.project.R
import com.parking.project.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var TAG: String? = null
    var progressDialog: ProgressDialog? = null
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog= ProgressDialog(this);
        progressDialog!!.setMessage(getString(R.string.please_wait));
        mAuth = FirebaseAuth.getInstance();
        binding.signupCall.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActiivty::class.java))
            finish()
        }
        binding.signinBtn.setOnClickListener {
            progressDialog!!.show()
            loginCall()

        }


    }

    private fun loginCall() {
        mAuth!!.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
            .addOnCompleteListener(this@LoginActivity) { task ->
                progressDialog!!.dismiss()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}