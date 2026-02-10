package com.example.jeffenger.utils.helper

fun normalizeCompanyId(rawCompany: String): String =
    rawCompany
        .trim()
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')