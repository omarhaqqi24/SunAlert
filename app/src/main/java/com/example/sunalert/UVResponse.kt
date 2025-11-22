package com.example.sunalert

data class UVResponse (
    val result: UVResult
)

data class UVResult (
    val uv: Double,
    val uv_max: Double?,
    val uv_max_time: String?
)