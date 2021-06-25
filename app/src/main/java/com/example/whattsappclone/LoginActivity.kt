package com.example.whattsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    private lateinit var countryCode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNumber.addTextChangedListener{
            if(!it.isNullOrEmpty()&&it.length>=10){
                btnNext.isEnabled=true
            }
        }

        btnNext.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode=ccp.selectedCountryCodeWithPlus
        phoneNumber=countryCode+etNumber.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number:${phoneNumber}\n"+
            "Is this Ok,or would you like to edit the number")

            setPositiveButton("Ok"){
                _,_->showOtpActivity()
            }
            setNegativeButton("Edit"){
                dialog,which->dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        var i=Intent(this,OtpActivity::class.java)
        i.putExtra("PHONE_NUMBER",phoneNumber)
        startActivity(i)
        finish()
    }
}