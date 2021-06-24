package com.example.whattsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etNumber.addTextChangedListener{
            if(!it.isNullOrEmpty()&&it.length>=10){
                btnNext.isEnabled=true
            }
        }

    }
}