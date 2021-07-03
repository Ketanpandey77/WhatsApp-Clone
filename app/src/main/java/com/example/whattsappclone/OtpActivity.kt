package com.example.whattsappclone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.Tasks
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity(), View.OnClickListener {
    var phoneNumber:String?=null
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId:String?=null
    private var mResendToken:PhoneAuthProvider.ForceResendingToken?=null
    private lateinit var progressDialog:ProgressDialog
    private var mCountDownTimer:CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initViews()
        startVerify()
    }

    private fun startVerify() {
        startPhoneNumberVerification(phoneNumber!!)
        showTimer(60000)
        progressDialog=createProgressDialog("Sending a verification code",false)
        progressDialog.show()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks
        )
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()
        tvVerify.text = "Verify $phoneNumber"
        setSpannableString()

        btnVerification.setOnClickListener(this)
        btnResend.setOnClickListener(this)

        //this is copied from Firebase Authentication
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsCode: String? = credential.smsCode
                if (!smsCode.isNullOrBlank()) {
                    etOtp.setText(smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException", e)
                    Log.e("=========:", "FirebaseAuthInvalidCredentialsException " + e.message)
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("HELLO","Failed1")
                    Log.e("Exception:", "FirebaseTooManyRequestsException", e)
                }
                // Show a message and update the UI
                // Show a message and update the UI

                notifyUserAndRetry("Your Phone Number might be wrong or connection error.Retry again!")
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }

    }


    private fun showTimer(miliSeconds: Long) {
        btnResend.isEnabled = false
        mCountDownTimer = object : CountDownTimer(miliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCounter.isVisible = true
                tvCounter.text = getString(R.string.time_remaining, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                btnResend.isEnabled = true
                tvCounter.isVisible = false
            }
        }.start()
    }

        override fun onDestroy() {
            super.onDestroy()
            if (mCountDownTimer != null) {
                mCountDownTimer!!.cancel()
            }
        }

        private fun setSpannableString() {
            val span = SpannableString(getString(R.string.waitingText, phoneNumber))

            val clickSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    //going back
                    startLoginActivity()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ds.linkColor
                    ds.isUnderlineText = false   //removes the underline from the text
                }
            }

            span.setSpan(clickSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvWaiting.movementMethod = LinkMovementMethod.getInstance()
            tvWaiting.text = span
        }

        override fun onBackPressed() {
            //commenting below line will disable back press button
            //super.onBackPressed()
        }


        private fun notifyUserAndRetry(s: String) {
            MaterialAlertDialogBuilder(this).apply {
                setMessage(s)
                setPositiveButton("Ok") { _, _ ->
                    startLoginActivity()
                }

                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

                setCancelable(false)
                create()
                show()
            }
        }

        private fun startLoginActivity() {
            val i = Intent(this, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
        }

        private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            if (::progressDialog.isInitialized) {
                                progressDialog.dismiss()
                            }
                            //First Time Login

                            if (task.result?.additionalUserInfo?.isNewUser == true) {
                                showSignUpActivity()
                            } else {
                              showSignUpActivity()  //for testing purpose
//                                showHomeActivity()
                            }
                        } else {

                            if (::progressDialog.isInitialized) {
                                progressDialog.dismiss()
                            }

                            notifyUserAndRetry("Your Phone Number Verification is failed.Retry again!")
                        }
                    }
        }

    private fun showHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun Context.createProgressDialog(message: String, isCancellable: Boolean): ProgressDialog {
            return ProgressDialog(this).apply {
                setCancelable(isCancellable)
                setMessage(message)
                setCanceledOnTouchOutside(false)
            }
        }

    override fun onClick(v: View?) {
        when (v) {
            btnVerification -> {
                // try to enter the code by yourself to handle the case
                // if user enter another sim card used in another phone ...
                var code = etOtp.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {

                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credential =
                            PhoneAuthProvider.getCredential(mVerificationId!!, code.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }

            btnResend -> {
                if (mResendToken != null) {
                    resendVerificationCode(phoneNumber.toString(), mResendToken)
                    showTimer(60000)
                    progressDialog = createProgressDialog("Sending a verification code", false)
                    progressDialog.show()
                } else {
                    Toast.makeText(this,"Sorry,Cant process",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        mResendToken: PhoneAuthProvider.ForceResendingToken?
        ) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks, // OnVerificationStateChangedCallbacks
                    mResendToken
            ) // ForceResendingToken from callbacks
    }
}