package com.ryanrvldo.data.network.response

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class ErrorResponse(
    @SerializedName("status_code") val statusCode: Int = 0,
    @SerializedName("status_message") val statusMessage: String = ""
)