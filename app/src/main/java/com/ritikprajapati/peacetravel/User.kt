package com.ritikprajapati.peacetravel

data class
User(
    val userName: String = "",
    val phoneNumber: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val time: String = "",
    val date: String = "",
    val id: String = "",
    var unreadMessagesCount: Int = 0 // Add this property
)
