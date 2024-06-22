package com.ritikprajapati.peacetravel

import java.io.Serializable
data class Message(
    val content: String = "",
    val senderUid: String = "",
    val date: String = "",
    var isSent: Boolean = false // true if the message is sent by the user, false if received

) : Serializable {
    constructor() : this ("", "","",false)
}
