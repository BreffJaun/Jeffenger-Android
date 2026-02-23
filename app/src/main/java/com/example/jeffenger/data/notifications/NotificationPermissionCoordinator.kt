package com.example.jeffenger.data.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.jeffenger.data.notifications.FcmTokenManager
import com.example.jeffenger.data.notifications.NotificationPrefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

@Composable
fun NotificationPermissionCoordinator(
    context: Context,
    db: FirebaseFirestore,
) {
    val scope = rememberCoroutineScope()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val alreadyAsked by NotificationPrefs.askedFlow(context).collectAsState(initial = false)

    var showRationale by remember { mutableStateOf(false) }

    // Result Launcher (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        scope.launch {
            NotificationPrefs.setAsked(context, true)

            if (granted && NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // Token holen & speichern (Firestore: deviceTokens)
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    FcmTokenManager.saveToken(token)
                }
                // Firestore Flag true
                db.collection("users").document(uid)
                    .update("notificationsEnabled", true)
            } else {
                // Firestore Flag false (wichtig!)
                db.collection("users").document(uid)
                    .update("notificationsEnabled", false)
            }
        }
    }

    fun requestPermissionIfNeeded() {
        // Android < 13: keine Runtime Permission. Hier kannst du direkt aktivieren,
        // aber nur wenn Notifications nicht systemweit aus sind.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            scope.launch {
                NotificationPrefs.setAsked(context, true)

                val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
                db.collection("users").document(uid).update("notificationsEnabled", enabled)

                if (enabled) {
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        FcmTokenManager.saveToken(token)
                    }
                }
            }
            return
        }

        val perm = Manifest.permission.POST_NOTIFICATIONS

        when {
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED -> {
                scope.launch {
                    NotificationPrefs.setAsked(context, true)
                    val enabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
                    db.collection("users").document(uid).update("notificationsEnabled", enabled)

                    if (enabled) {
                        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                            FcmTokenManager.saveToken(token)
                        }
                    }
                }
            }

            // Rationale anzeigen (2+ mal abgelehnt / “nicht mehr fragen” Situation ist tricky,
            // aber Rationale ist trotzdem der richtige Weg, inkl. Settings Deep-Link)
            androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                (context as android.app.Activity),
                perm
            ) -> {
                showRationale = true
            }

            else -> {
                permissionLauncher.launch(perm)
            }
        }
    }

    // Trigger: sobald eingeloggt UND noch nicht gefragt.
    LaunchedEffect(uid, alreadyAsked) {
        if (!alreadyAsked) {
            requestPermissionIfNeeded()
        }
    }

    if (showRationale) {
        NotificationPermissionRationaleDialog(
            context = context,
            onDismiss = { showRationale = false },
            onTryAgain = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        )
    }
}