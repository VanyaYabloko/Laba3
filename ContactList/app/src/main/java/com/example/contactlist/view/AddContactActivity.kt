package com.example.contactlist.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.contactlist.R
import com.example.contactlist.databinding.ActivityAddContactBinding
import com.example.contactlist.model.Contact
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private var photoUri: Uri? = null

    // Регистрация лаунчера для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePhoto()
        } else {
            showPermissionDeniedMessage()
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                binding.contactPhotoPreview.setImageURI(uri)
                binding.contactPhotoPreview.contentDescription = getString(R.string.contact_photo_taken)
            }
        } else {
            Toast.makeText(this, R.string.photo_capture_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.takePhotoButton.setOnClickListener {
            checkCameraPermission()
        }

        binding.addButton.setOnClickListener {
            if (validateInputs()) {
                saveContactAndFinish()
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePhoto()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationale()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePhoto() {
        try {
            val photoFile = createImageFile() ?: throw IOException("Couldn't create file")
            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            ).also { uri ->
                binding.contactPhotoPreview.tag = photoFile.absolutePath
            }
            takePhotoLauncher.launch(photoUri)
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir("Pictures")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun saveContactAndFinish() {
        val photoPath = binding.contactPhotoPreview.tag?.toString() ?: ""
        val contact = Contact(
            id = 0,
            name = binding.nameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            phone = binding.phoneEditText.text.toString(),
            photoPath = photoPath
        )

        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(MainActivity.EXTRA_CONTACT, contact)
        })
        finish()
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        with(binding) {
            if (nameEditText.text.isNullOrBlank()) {
                nameEditText.error = getString(R.string.name_required)
                isValid = false
            }

            if (emailEditText.text.isNullOrBlank()) {
                emailEditText.error = getString(R.string.email_required)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
                emailEditText.error = getString(R.string.invalid_email)
                isValid = false
            }

            if (phoneEditText.text.isNullOrBlank()) {
                phoneEditText.error = getString(R.string.phone_required)
                isValid = false
            }
        }

        return isValid
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_needed)
            .setMessage(R.string.camera_permission_rationale)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            R.string.camera_permission_denied,
            Toast.LENGTH_LONG
        ).show()
    }
}