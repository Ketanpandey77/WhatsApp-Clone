package com.example.whattsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var downloadUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        ivProfile.setOnClickListener {
            checkPermissionForImage()
        }
    }

    private fun checkPermissionForImage() {
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        ) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            requestPermissions(
                permission,
                1001
            ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
            requestPermissions(
                permissionWrite,
                1002
            ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            data?.data?.let {
                ivProfile.setImageURI(it)
                startUpload(it)
            }
        }
    }

    private fun startUpload(it: Uri) {
        btnNext.isEnabled=false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                btnNext.isEnabled = true
            } else {
                btnNext.isEnabled = true
            }
        }.addOnFailureListener {

        }
    }
}
