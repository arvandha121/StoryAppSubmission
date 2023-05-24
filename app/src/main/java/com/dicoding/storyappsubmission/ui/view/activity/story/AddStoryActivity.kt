package com.dicoding.storyappsubmission.ui.view.activity.story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyappsubmission.R
import com.dicoding.storyappsubmission.databinding.ActivityAddStoryBinding
import com.dicoding.storyappsubmission.remote.UserInstance
import com.dicoding.storyappsubmission.remote.api.ApiConfig
import com.dicoding.storyappsubmission.remote.response.story.addstory.AddStoryResponse
import com.dicoding.storyappsubmission.ui.createCustomTempFile
import com.dicoding.storyappsubmission.ui.reduceFileImage
import com.dicoding.storyappsubmission.ui.uriToFile
import com.dicoding.storyappsubmission.ui.view.activity.story.model.AddStoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private lateinit var location: Location
    private lateinit var fusedLocation: FusedLocationProviderClient

    fun String?.generateToken(): String? = if (!this.isNullOrEmpty()) "Bearer $this" else null
    private val pref = UserInstance.getInstance(dataStore)

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.imagePreview.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { Maps ->
            when {
                Maps.get(Manifest.permission.ACCESS_FINE_LOCATION) ?: false -> {
                    getMyLastLocation()
                }
                Maps.get(Manifest.permission.ACCESS_COARSE_LOCATION) ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Maps.getValue(getString(0))
                }
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.addStory)

        viewModel =
            ViewModelProvider(this, AddStoryViewModel.Factory(pref))[AddStoryViewModel::class.java]

        Permission()

        binding.descriptions.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    binding.uploadButton.isEnabled = true
                } else {
                    binding.uploadButton.isEnabled = false
                    binding.descriptions.error = getString(R.string.fill_in)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        onClicked()
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    private fun Permission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun onClicked() {
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { startUpload() }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(packageManager)
        }

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.dicoding.storyappsubmission",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choosePicture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startUpload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding.descriptions.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                PHOTO,
                file.name,
                requestImageFile
            )
            val lat = location.latitude.toFloat()
            val long = location.longitude.toFloat()

            viewModel.getToken().observe(
                this
            ) { token: String ->
                val service = ApiConfig.getApiService()
                    .addStories("Bearer $token", imageMultipart, description, lat, long)
                service.enqueue(object : Callback<AddStoryResponse> {
                    override fun onResponse(
                        call: Call<AddStoryResponse>,
                        response: Response<AddStoryResponse>
                    ) {
                        val responseBody = response.body()
                        if (response.isSuccessful) {
                            if (responseBody != null && !responseBody.error!!) {
                                Toast.makeText(
                                    this@AddStoryActivity,
                                    responseBody.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                intent()
                            }
                        } else {
                            Toast.makeText(
                                this@AddStoryActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                        Toast.makeText(
                            this@AddStoryActivity,
                            FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.choosePicture),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun intent() {
        startActivity(Intent(this, StoryActivity::class.java))
        finish()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocation.lastLocation.addOnSuccessListener { locs: Location? ->
                if (locs != null) {
                    location = locs
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

//    private fun getMyLastLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            fusedLocation.lastLocation.addOnSuccessListener {
//                if (it != null) {
//                    location = it
//                } else {
//                    Toast.makeText(
//                        this@AddStoryActivity,
//                        getString(R.string.location),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        } else {
//            requestPermissionLauncher.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        }
//    }

    companion object {
        const val FAILED = "failed"
        const val PHOTO = "photo"
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
