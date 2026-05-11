package com.example.myapplication.data.repository

import android.net.Uri
import com.example.myapplication.data.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReportRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadReport(
        userId: String,
        imageUri: Uri,
        wasteType: String,
        lat: Double,
        lng: Double,
        description: String
    ): Result<Unit> {
        return try {
            // 1. Upload Image to Firebase Storage
            val fileName = UUID.randomUUID().toString()
            val imageRef = storage.reference.child("reports/$fileName")
            imageRef.putFile(imageUri).await()
            val imageUrl = imageRef.downloadUrl.await().toString()

            // 2. Save Report to Firestore
            val reportId = firestore.collection("reports").document().id
            val report = Report(
                reportId = reportId,
                userId = userId,
                imageUrl = imageUrl,
                wasteType = wasteType,
                latitude = lat,
                longitude = lng,
                description = description
            )
            firestore.collection("reports").document(reportId).set(report).await()

            // 3. Award Eco Points (Example: 50 points per report)
            val userRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("ecoPoints") ?: 0
                transaction.update(userRef, "ecoPoints", currentPoints + 50)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getReports(): Flow<List<Report>> = callbackFlow {
        val subscription = firestore.collection("reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(Report::class.java)
                    trySend(reports)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateReportStatus(reportId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("reports").document(reportId)
                .update("status", status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
