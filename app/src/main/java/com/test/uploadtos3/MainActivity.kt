package com.test.uploadtos3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.test.uploadtos3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mSelectFileUri: Uri
    private lateinit var mBinding: ActivityMainBinding
    private val  uploadViewModel: S3UploadViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding=ActivityMainBinding.inflate(layoutInflater)
        mBinding.act=this
        mBinding.lifecycleOwner=this

        setContentView(mBinding.root)

        /*   ImagePicker.with(this)
               .crop()	    			//Crop image(Optional), Check Customization for more option
               .compress(1024)			//Final image size will be less than 1 MB(Optional)
               .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
               .start()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                mSelectFileUri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                mBinding.imageView.setImageURI(mSelectFileUri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onClick(view: View){
        when(view.id){

            R.id.selectImageFromGallery->{
                ImagePicker.with(this)
                    .galleryOnly()	//User can only select image from Gallery
                    .start()
            }
            R.id.selectImageFromCamara->{
                ImagePicker.with(this)
                    .cameraOnly()	//User can only select image from Gallery
                    .start()
            }
            R.id.uploadButton->{


                uploadViewModel.uploadFile(mSelectFileUri)
            }
        }
    }
}