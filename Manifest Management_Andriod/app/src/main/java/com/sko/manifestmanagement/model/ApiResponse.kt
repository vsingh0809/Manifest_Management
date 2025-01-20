package com.sko.manifestmanagement.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)



