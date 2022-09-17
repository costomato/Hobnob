package com.project.hobnob.model

import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val number: Int,
    val size: Int
)
