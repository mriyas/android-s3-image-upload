package com.test.uploadtos3

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.amazonaws.services.s3.model.CannedAccessControlList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*


class S3UploadViewModel(val mApplication: Application) : AndroidViewModel(mApplication) {
    private lateinit var mTransferUtility: TransferUtility
    private val TAG = javaClass.simpleName

    private companion object {
        const val YOUR_ACCESS_KEY = "******"
        const val YOUR_SECRET_KEY = "***************"
        const val YOUR_BUCKET = "*********"
        val YOUR_REGION = Regions.AP_SOUTH_1
        const val YOUR_REGION_PATH = "ap-south-1"
        const val END_POINT_URL = "https://s3-${YOUR_REGION_PATH}.amazonaws.com/"


    }


    init {

        // initAws()

    }

    private fun doFileTransfer(file: File) {


        val mCredentials = BasicAWSCredentials(YOUR_ACCESS_KEY, YOUR_SECRET_KEY)
        val s3: AmazonS3 = AmazonS3Client(mCredentials)
        // Security.setProperty("networkaddress.cache.ttl", "60")
        s3.setRegion(Region.getRegion(YOUR_REGION))
        s3.setEndpoint(END_POINT_URL)
        val buckets: List<Bucket> = s3.listBuckets()
        for (bucket in buckets) {
            Log.i(
                TAG,
                "Bucket Name " + bucket.name.toString() + " Owner " + bucket.owner
                    .toString() + " Date " + bucket.creationDate
            )
        }
        Log.d("TAG ", "Size : " + s3.listBuckets().size)
        mTransferUtility = TransferUtility.builder().s3Client(s3).context(mApplication).build()
        val objectKey = "android_${System.currentTimeMillis()}";
        val access = CannedAccessControlList.Private

        val observer: TransferObserver =
            mTransferUtility.upload(YOUR_BUCKET, objectKey, file, access)
        Log.d("TAG ", "Trying to upload : " + observer.absoluteFilePath)
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {


                if (state == TransferState.COMPLETED) {
                    val url =
                        "https://" + YOUR_REGION + ".s3.amazonaws.com/$YOUR_BUCKET/" + observer.key
                    val url2 =
                        "https://" + YOUR_REGION + ".s3.amazonaws.com/$YOUR_BUCKET/" + file.name

                    Log.i(
                        TAG,
                        " onStateChanged() >> URL=$url.png"
                    )
                    Log.d(TAG, " onStateChanged() >> URL=$url2.png")
                } else {
                    // do something
                    //  progress.hide()
                    Log.d(
                        TAG, " onStateChanged() >> " +
                                """
                ID $id
                State ${state.name}
                Image ID $objectKey
                """.trimIndent()
                    )
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentage = (bytesCurrent / bytesTotal * 100).toInt()
                Log.e(TAG, "onProgressChanged() >> Uploading... $percentage %")

                // progress.setProgress(percentage)
                //Display percentage transfered to user
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }
        })

    }

    fun uploadFile(fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {


            val file = getFile(fileUri) ?: return@launch
            doFileTransfer(file)

        }
    }

    private fun getFile(fileUri: Uri): File? {

        try {
            val inputStream: InputStream? =
                mApplication.contentResolver.openInputStream(fileUri!!) //to read a file from content path

            val file = File.createTempFile("image", "fileToUpload")

            val outStream: OutputStream =
                FileOutputStream(file)//creating stream pipeline to the file

            outStream.write(inputStream!!.readBytes())//passing bytes of data to the filestream / Write array of byte to current output stream

            outStream.close()
            return file
        } catch (ex: IOException) {
            return null
        } catch (ex: Exception) {
            return null
        }

    }
}