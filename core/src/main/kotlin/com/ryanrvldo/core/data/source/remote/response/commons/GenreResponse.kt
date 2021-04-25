package com.ryanrvldo.core.data.source.remote.response.commons

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
)
