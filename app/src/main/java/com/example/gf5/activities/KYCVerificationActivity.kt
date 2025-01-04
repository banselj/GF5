package com.example.gf5.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.databinding.ActivityKycVerificationBinding
import com.example.gf5.viewmodels.KYCViewModel
import com.example.gf5.viewmodels.KYCResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class KYCVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycVerificationBinding
    private val kycViewModel: KYCViewModel by viewModels()
    private var documentUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "KYC Verification"

        binding.uploadDocumentButton.setOnClickListener {
            openDocumentPicker()
        }

        binding.submitKycButton.isEnabled = false
        binding.submitKycButton.setOnClickListener {
            handleSubmitKYC()
        }

        observeKYCStatus()

        // Initialize document picker launcher
        documentPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                documentUri = uri
                binding.documentStatusText.text = "Document selected: ${uri.lastPathSegment}"
                binding.submitKycButton.isEnabled = true
            } else {
                Toast.makeText(this, "No document selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openDocumentPicker() {
        documentPickerLauncher.launch("application/pdf")
    }

    private fun handleSubmitKYC() {
        documentUri?.let { uri ->
            lifecycleScope.launch {
                kycViewModel.submitKycDocument(uri).collect { result ->
                    when (result) {
                        is KYCResult.Success -> {
                            Log.d(TAG, "KYC submitted successfully")
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@KYCVerificationActivity, "KYC submitted successfully", Toast.LENGTH_SHORT).show()
                        }
                        is KYCResult.Error -> {
                            Log.e(TAG, "Failed to submit KYC: ${result.message}")
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@KYCVerificationActivity, "Failed to submit KYC: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            binding.progressBar.visibility = View.VISIBLE
        } ?: run {
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

    companion object {
        private const val TAG = "KYCVerificationActivity"
    }
}