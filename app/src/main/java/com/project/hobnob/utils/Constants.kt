package com.project.hobnob.utils

object Constants {
        private const val BASE_URL = "https://www.xetra.biz/api/v1"
//    private const val BASE_URL = "http://192.168.167.141:8080/api/v1"   // mac
//    const val SOCKET_URL = "http://192.168.167.141:8080"   // mac
        const val SOCKET_URL = "https://www.xetra.biz"   // mac
    const val CREATE_NEW_USER = "$BASE_URL/createNewUser"
    const val AUTHENTICATE_USER = "$BASE_URL/authenticateUser"
    const val UPDATE_USER = "$BASE_URL/updateUser"
    const val DELETE_USER = "$BASE_URL/deleteUser"
    const val GET_RESET_LINK = "$BASE_URL/getResetLink"
    const val SEND_OTP = "$BASE_URL/sendOtp"
    const val VERIFY_OTP = "$BASE_URL/verifyOtp"
    const val SEND_MESSAGE = "$BASE_URL/setMessage"
    const val SEND_ROOM_MESSAGE = "$BASE_URL/setRoomMessage"
    const val GET_MESSAGE = "$BASE_URL/getMessage"
    const val GET_ROOM_MESSAGES = "$BASE_URL/getRoomMessages"
    const val DELETE_ROOM_MESSAGE = "$BASE_URL/deleteRoomMessage"
    const val USER_PREFERENCES = "user_preferences"
    const val HEADER_KEY = "api-key"
    lateinit var API_KEY: String
    const val USER_EMAIL = "email"
    const val USER_NAME = "username"
    const val USER_ID = "user_id"
    const val USER_PASS = "user_pass"
    const val ABOUT_VERIFIED = "aboutVerified"
    const val IS_VERIFIED = "isVerified"
    const val IS_TEACHER = "isTeacher"
}