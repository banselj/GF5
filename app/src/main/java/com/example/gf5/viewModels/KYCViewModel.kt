package com.example.gf5.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class KYCViewModel : ViewModel() {

    sealed class KYCStatus {
        object Verified : KYCStatus()
        object Pending : KYCStatus()
        object Rejected : KYCStatus()
    }

    private val _kycStatus = MutableLiveData<KYCStatus>()
    val kycStatus: LiveData<KYCStatus> = _kycStatus

    sealed class KYCResult {
        data class Success(val message: String) : KYCResult()
        data class Error(val message: String) : KYCResult()
    }

    fun submitKycDocument(document: Uri): Flow<KYCResult> {
        return MutableStateFlow(KYCResult.Success("Document submitted successfully"))
    }
}