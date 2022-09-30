package com.test.uploadtos3

import android.R.attr.path
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.Bucket
import java.io.*
import java.security.Security


class S3UploadViewModel(val mApplication: Application): AndroidViewModel(mApplication) {
    private lateinit var mTransferUtility: TransferUtility
    private val TAG=javaClass.simpleName

    init {

       // initAws()

    }

    private fun initAws() {
        val  ACCESS_KEY="****************"
        val SECRET_KEY="****************"

        val mCredentials =  BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        val s3: AmazonS3 = AmazonS3Client(mCredentials)
        Security.setProperty("networkaddress.cache.ttl", "60")
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1))
        s3.setEndpoint("https://s3-ap-southeast-1.amazonaws.com/")
        val buckets: List<Bucket> = s3.listBuckets()
        for (bucket in buckets) {
            Log.i(TAG,
                "Bucket Name " + bucket.getName().toString() + " Owner " + bucket.getOwner()
                    .toString() + " Date " + bucket.getCreationDate()
            )
        }
        Log.d("TAG ", "Size : " + s3.listBuckets().size)
        mTransferUtility = TransferUtility.builder().s3Client(s3).context(mApplication).build()
    }

    fun uploadFile(fileUri:Uri){
       val file = getFile(fileUri) ?: return
        initAws()
        val MY_BUCKET="bucket_name"
        val OBJECT_KEY="unique_id";
        val observer: TransferObserver =
            mTransferUtility.upload(MY_BUCKET, OBJECT_KEY, file)
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                // do something
              //  progress.hide()
                Log.d(TAG,
                    """
                ID $id
                State ${state.name}
                Image ID $OBJECT_KEY
                """.trimIndent()
                )
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentage = (bytesCurrent / bytesTotal * 100).toInt()
               // progress.setProgress(percentage)
                //Display percentage transfered to user
            }

            override fun onError(id: Int, ex: Exception) {
                // do something
                Log.e("Error  ", "" + ex)
            }
        })
    }

    private fun getFile(fileUri: Uri): File? {

        try {
            val inputStream: InputStream? = mApplication.contentResolver.openInputStream(fileUri!!) //to read a file from content path

            val file = File.createTempFile("image","fileToUpload")

            val outStream: OutputStream = FileOutputStream(file)//creating stream pipeline to the file

            outStream.write(inputStream!!.readBytes())//passing bytes of data to the filestream / Write array of byte to current output stream

            outStream.close()
            return file
        }catch (ex: IOException){
            return null
        }catch (ex: Exception){
            return null
        }

    }
}