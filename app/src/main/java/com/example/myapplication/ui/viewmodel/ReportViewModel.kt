package com.example.myapplication.ui.viewmodel

import android.content.Context
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

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

    init {
        observeReports()
    }

    private fun observeReports() {
        viewModelScope.launch {
            repository.getReports().collect { list ->
                _reports.value = list
            }
        }
    }

    fun submitReport(
        context: Context,
        userId: String,
        imageUri: Uri,
        wasteType: String,
        lat: Double,
        lng: Double,
        description: String
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            val result = repository.uploadReport(context, userId, imageUri, wasteType, lat, lng, description)
            if (result.isSuccess) {
                _reportState.value = ReportState.Success
            } else {
                _reportState.value = ReportState.Error(result.exceptionOrNull()?.message ?: "Upload Failed")
            }
        }
    }

    fun markAsCleaned(reportId: String, volunteerId: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, volunteerId, "Cleaned")
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
