package com.example.myapplication.data.repository

import com.example.myapplication.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun signIn(email: String, orgPassword: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, orgPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(name: String, email: String, orgPassword: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, orgPassword).await()
            val userId = result.user?.uid ?: throw Exception("User creation failed")
            
            val user = User(uid = userId, name = name, email = email)
            firestore.collection("users").document(userId).set(user).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
