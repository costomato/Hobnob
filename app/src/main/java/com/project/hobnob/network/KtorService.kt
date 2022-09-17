package com.project.hobnob.network

import com.project.hobnob.model.Message
import com.project.hobnob.model.Page
import com.project.hobnob.model.ResponseData
import com.project.hobnob.model.UserSchema
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.flow.Flow

interface KtorService {
    suspend fun createUser(userSchema: UserSchema): ResponseData?

    suspend fun authenticateUser(userSchema: UserSchema): ResponseData?

    suspend fun updateUser(newData: UserSchema): ResponseData?

    suspend fun sendResetLink(email: UserSchema): ResponseData?

    suspend fun deleteUser(userSchema: UserSchema): ResponseData?

    suspend fun sendOtp(userSchema: UserSchema): ResponseData?

    suspend fun verifyOtp(userSchema: UserSchema): ResponseData?

    suspend fun getRoomMessages(page: Page): List<Message>?

    companion object {
        fun create(): KtorService {
            return KtorServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                }
            )
        }
    }
}