package com.example.jeffenger.utils.normalization

import java.text.Normalizer

fun normalizeCompanyId(rawCompany: String): String {
    // Ö -> OE | Ü -> UE | Ä -> AE
    val normalized = Normalizer.normalize(rawCompany, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

    return normalized
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
}