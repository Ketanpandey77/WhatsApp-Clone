package com.example.whattsappclone

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
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
        showTimer(30000)
//        progressDialog=createProgressDialog("Sending a verification code",false)
//        progressDialog.show()
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                30,
                TimeUnit.SECONDS,
                this,
                callbacks
        )
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()
        tvVerify.text="Verify $phoneNumber"
        setSpannableString()

        //this is copied from Firebase Authentication
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                Toast.makeText(this@OtpActivity,"onVerificationCompleted",Toast.LENGTH_SHORT).show()
                Log.d("ERROR_MESSAGE", "onVerificationCompleted:$credential")
                val smsCode:String?=credential.smsCode
                if(!smsCode.isNullOrBlank()){
                    etOtp.setText(smsCode)
                }
                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                Toast.makeText(this@OtpActivity,"onVerificationFailed",Toast.LENGTH_SHORT).show()
                Log.d("ERROR_MESSAGE", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
                // Show a message and update the UI
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()
                Toast.makeText(this@OtpActivity,"onCodeSent",Toast.LENGTH_SHORT).show()
                Log.d("ERROR_MESSAGE", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: Any) {

    }

    private fun showTimer(miliSeconds: Long) {
        btnResend.isEnabled=false
        mCountDownTimer= object : CountDownTimer(miliSeconds,1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCounter.isVisible=true
                tvCounter.text=getString(R.string.time_remaining,millisUntilFinished/1000)
            }
            override fun onFinish() {
                btnResend.isEnabled=true
                tvCounter.isVisible=false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCountDownTimer!=null){
            mCountDownTimer!!.cancel()
        }
    }

    private fun setSpannableString() {
        val span=SpannableString(getString(R.string.waitingText,phoneNumber))

        val clickSpan=object : ClickableSpan() {
            override fun onClick(widget: View) {
                //going back
                startLoginActivity()
            }
            override fun updateDrawState(ds: TextPaint) {
                ds.color=ds.linkColor
                ds.isUnderlineText=false   //removes the underline from the text
            }
        }

        span.setSpan(clickSpan,span.length-13,span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvWaiting.movementMethod=LinkMovementMethod.getInstance()
        tvWaiting.text=span
    }

    private fun startLoginActivity() {
        val i=Intent(this,LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    override fun onBackPressed() {
        //commenting below line will disable back press button
        //super.onBackPressed()
    }
}
fun Context.createProgressDialog(message:String,isCancellable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply{
        setCancelable(isCancellable)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }
}