package com.project.hobnob.network

import com.project.hobnob.model.Message
import com.project.hobnob.model.Page
import com.project.hobnob.model.ResponseData
import com.project.hobnob.model.UserSchema
import com.project.hobnob.utils.Constants
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorServiceImpl(
    private val client: HttpClient
) : KtorService {

    override suspend fun createUser(userSchema: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.CREATE_NEW_USER)
                contentType(ContentType.Application.Json)
                body = userSchema
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun authenticateUser(userSchema: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.AUTHENTICATE_USER)
                contentType(ContentType.Application.Json)
                body = userSchema
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun updateUser(newData: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.UPDATE_USER)
                contentType(ContentType.Application.Json)
                body = newData
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun sendResetLink(email: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.GET_RESET_LINK)
                contentType(ContentType.Application.Json)
                body = email
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun deleteUser(userSchema: UserSchema): ResponseData? {
        return try {
            client.delete {
                url(Constants.DELETE_USER)
                contentType(ContentType.Application.Json)
                body = userSchema
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun sendOtp(userSchema: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.SEND_OTP)
                contentType(ContentType.Application.Json)
                body = userSchema
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun verifyOtp(userSchema: UserSchema): ResponseData? {
        return try {
            client.post {
                url(Constants.VERIFY_OTP)
                contentType(ContentType.Application.Json)
                body = userSchema
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException) {
            // 4xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException) {
            // 5xx - responses
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    override suspend fun getRoomMessages(page: Page): List<Message>? {
        return try {
            client.post {
                url(Constants.GET_ROOM_MESSAGES)
                contentType(ContentType.Application.Json)
                body = page
                header(Constants.HEADER_KEY, Constants.API_KEY)
            }
        } catch (e: Exception) {
            println("Error: ${e.printStackTrace()}")
            null
        }
    }


/*
    sample get request

    client.get { url(Constants.GET_ALL_USERS) }
*/

}