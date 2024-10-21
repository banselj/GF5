package com.example.gf5.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.databinding.ActivityKycVerificationBinding
import com.example.gf5.viewmodels.KYCViewModel
import kotlinx.coroutines.launch

class KYCVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycVerificationBinding
    private val kycViewModel: KYCViewModel by viewModels()
    private var documentUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the action bar title
        supportActionBar?.title = "KYC Verification"

        // Set up button to upload documents
        binding.uploadDocumentButton.setOnClickListener {
            openDocumentPicker()
        }

        // Set up submit button
        binding.submitKycButton.setOnClickListener {
            handleSubmitKYC()
        }

        // Observe KYC status updates
        observeKYCStatus()
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        documentPickerLauncher.launch(intent)
    }

    private val documentPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.also { uri ->
                documentUri = uri
                binding.documentStatusText.text = "Document selected: ${uri.lastPathSegment}"
            }
        }
    }

    private fun handleSubmitKYC() {
        val document = documentUri
        if (document != null) {
            lifecycleScope.launch {
                kycViewModel.submitKycDocument(document).collect { result ->
                    when (result) {
                        is KYCViewModel.KYCResult.Success -> {
                            Toast.makeText(this@KYCVerificationActivity, "KYC submitted successfully", Toast.LENGTH_SHORT).show()
                        }
                        is KYCViewModel.KYCResult.Error -> {
                            Toast.makeText(this@KYCVerificationActivity, "Failed to submit KYC: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please upload a valid document before submitting", Toast.LENGTH_LONG).show()
        }
    }

    private fun observeKYCStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kycViewModel.kycStatus.collect { status ->
                    when (status) {
                        is KYCViewModel.KYCStatus.Verified -> {
                            binding.kycStatusText.text = "KYC Status: Verified"
                        }
                        is KYCViewModel.KYCStatus.Pending -> {
                            binding.kycStatusText.text = "KYC Status: Pending"
                        }
                        is KYCViewModel.KYCStatus.Rejected -> {
                            binding.kycStatusText.text = "KYC Status: Rejected. Please resubmit."
                        }
                        else -> {
                            binding.kycStatusText.text = "KYC Status: Unknown"
                        }
                    }
                }
            }
        }
    }
}
