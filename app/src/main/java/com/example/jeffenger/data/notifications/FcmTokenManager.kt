package com.example.jeffenger.data.notifications

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FcmTokenManager {

    fun saveToken(token: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(
                "deviceTokens",
                FieldValue.arrayUnion(token)
            )
            .addOnSuccessListener {
                Log.d("FCM", "Token saved")
            }
            .addOnFailureListener {
                Log.e("FCM", "Token save failed: ${it.message}")
            }
    }

    fun removeToken(token: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(
                "deviceTokens",
                FieldValue.arrayRemove(token)
            )
    }
}