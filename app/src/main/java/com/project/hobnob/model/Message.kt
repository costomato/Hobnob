package com.project.hobnob.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val _id: String? = null,
    val body: String? = null,
    val sender: String? = null,
    val senderName: String? = null,
    val receiver: String? = null,
    val timestamp: String? = null
)
