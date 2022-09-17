package com.project.hobnob.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSchema(
    val _id: String? = null,
    val __v: Int? = null,
    val createdAt: String? = null,
    val email: String? = null,
    val isTeacher: Boolean? = null,
    val name: String? = null,
    val password: String? = null,
    val resetPasswordExpire: Long? = null,
    val resetPasswordToken: String? = null,
    val isVerified: Boolean? = null,
    val _otp: String? = null
)