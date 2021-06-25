package com.example.whattsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_otp.*

val PHONE_NUMBER="phoneNumber"

class OtpActivity : AppCompatActivity() {
    var phoneNumber:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        initViews()
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
        tvVerify.text="Verify $phoneNumber"
    }
}