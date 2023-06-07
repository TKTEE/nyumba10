package com.example.nyumba10

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.net.URI

class AddhousesActivity : AppCompatActivity() {
    lateinit var edtHouseNumber: EditText
    lateinit var edtHouseSize: EditText
    lateinit var edtHousePrice: EditText
    lateinit var imageView: ImageView
    lateinit var btnUpoad: Button
    lateinit var progress: ProgressDialog
    val Pick_Image_Request = 100
    lateinit var mAuth: FirebaseAuth
    lateinit var filePath: Uri
    lateinit var firebaseStore: FirebaseStorage
    lateinit var storageRef: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addhouses)
        edtHouseNumber = findViewById(R.id.mEdtHouseNumber)
        edtHouseSize = findViewById(R.id.mEdtSize)
        edtHousePrice = findViewById(R.id.mEdtPrice)
        imageView = findViewById(R.id.mImgPhoto)
        btnUpoad = findViewById(R.id.mBtnUpload)
        progress = ProgressDialog(this)
        progress.setTitle("Uploading")
        progress.setMessage("Please wait....")
        firebaseStore = FirebaseStorage.getInstance()
        storageRef = firebaseStore.getReference()
        mAuth = FirebaseAuth.getInstance()
        imageView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select an image"),
                Pick_Image_Request
            )
        }
        btnUpoad.setOnClickListener {
var houseNumber = edtHouseNumber.text.toString().trim()
var houseSize = edtHouseSize.text.toString().trim()
var housePrice = edtHousePrice.text.toString()
            var imageId = System.currentTimeMillis().toString()
            var userId = mAuth.currentUser?.uid
            if (houseNumber.isEmpty()){
                edtHouseNumber.setError("Please fill this input")
                edtHouseNumber.requestFocus()
            }else if (housePrice.isEmpty()){
                edtHousePrice.setError("Please fill this input")
                edtHousePrice.requestFocus()
            }else if (houseSize.isEmpty()){
                edtHouseSize.setError("Please fill this input")
                edtHouseSize.requestFocus()
            }else{
                if (filePath == null){
                    Toast.makeText(this, "Choose an image", Toast.LENGTH_SHORT).show()
                }else{
                    var ref = storageRef.child("Houses/$imageId")
                    progress.show()
                    ref.putFile(filePath).addOnCompleteListener(){
                        progress.dismiss()
                        if (it.isSuccessful){
                            ref.downloadUrl.addOnSuccessListener{
                                var imageurl= it.toString()
                                var houseData = House(houseNumber,houseSize,housePrice,userId!!,imageId,imageurl)
                                var dbRef = FirebaseDatabase.getInstance()
                                    .getReference().child("House/$imageId")
                            dbRef.setValue(houseData)
                            Toast.makeText(applicationContext, "Upload successful", Toast.LENGTH_SHORT).show()
                        }

                        }else{
                            Toast.makeText(applicationContext,it.exception!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Pick_Image_Request && resultCode == Activity.RESULT_OK)
            if (data == null || data.data == null) {
                return
            }

        filePath = data!!.data!!
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            imageView.setImageBitmap(bitmap)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}