package com.example.myapplication.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Report
import com.example.myapplication.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: ReportRepository = ReportRepository()) : ViewModel() {

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState

    val reports: StateFlow<List<Report>> = MutableStateFlow(emptyList())

    init {
        observeReports()
    }

    private fun observeReports() {
        viewModelScope.launch {
            repository.getReports().collect { list ->
                (reports as MutableStateFlow).value = list
            }
        }
    }

    fun submitReport(
        userId: String,
        imageUri: Uri,
        wasteType: String,
        lat: Double,
        lng: Double,
        description: String
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            val result = repository.uploadReport(userId, imageUri, wasteType, lat, lng, description)
            if (result.isSuccess) {
                _reportState.value = ReportState.Success
            } else {
                _reportState.value = ReportState.Error(result.exceptionOrNull()?.message ?: "Upload Failed")
            }
        }
    }

    fun markAsCleaned(reportId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, "Cleaned")
        }
    }

    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}
