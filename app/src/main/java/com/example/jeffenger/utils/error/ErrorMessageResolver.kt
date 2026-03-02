package com.example.jeffenger.utils.error

object ErrorMessageResolver {

    fun resolve(error: AppError): String {

        return when (error) {

            is AppError.Auth -> {
                when (error.reason) {
                    AuthReason.INVALID_CREDENTIALS ->
                        "E-Mail oder Passwort ist falsch."

                    AuthReason.USER_NOT_FOUND ->
                        "Kein Benutzer mit dieser E-Mail gefunden."

                    AuthReason.USER_DISABLED ->
                        "Dieses Konto wurde deaktiviert."

                    AuthReason.TOO_MANY_ATTEMPTS ->
                        "Zu viele Versuche. Bitte später erneut versuchen."

                    AuthReason.UNKNOWN ->
                        "Authentifizierung fehlgeschlagen."
                }
            }

            AppError.Network ->
                "Netzwerkproblem. Bitte Verbindung prüfen."

            AppError.PermissionDenied ->
                "Keine Berechtigung für diese Aktion."

            is AppError.Server ->
                "Serverfehler. Bitte später erneut versuchen."

            is AppError.Validation ->
                error.message

            is AppError.Unknown ->
                "Etwas ist schiefgelaufen."
        }
    }
}