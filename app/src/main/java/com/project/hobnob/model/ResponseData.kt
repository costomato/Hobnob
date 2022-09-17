package com.project.hobnob.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val statusOk: Boolean,
    val statusString: String?,
    val _id: String? = null,
    val _name: String? = null,
    val _isVerified: Boolean? = null,
    val _isTeacher: Boolean? = null
)