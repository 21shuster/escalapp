package es.eoi.proyectofinal.modelo

import com.google.firebase.firestore.FieldValue
import java.io.Serializable
import java.util.*

class Chat : Serializable{
    var idChat: String? = null

    constructor() {}

    constructor(idChat: String) {
        this.idChat = idChat
    }
}

class ChatUser : Serializable{
    var idChat: String? = null
    var idOtherUser: String? = null
    var nameOtherUser: String? = null
    var pendingCount: Int = 0

    constructor() {}

    constructor(idChat: String, idOtherUser: String, nameOtherUser: String, pendingCount: Int) {
        this.idChat = idChat
        this.idOtherUser = idOtherUser
        this.nameOtherUser = nameOtherUser
        this.pendingCount = pendingCount
    }

    override fun toString(): String {
        return "ChatUser(idChat=$idChat, idOtherUser=$idOtherUser, nameOtherUser=$nameOtherUser, pendingCount=$pendingCount)"
    }
}


class Message : Serializable {

    public var data: String? = null
    public var date: Date = Date()
    //public var timestamp: FieldValue? = FieldValue.serverTimestamp()
    public var read: Boolean = false
    public var sender: String? = null
    public var ubi: Ubicacion? = null

    constructor() {}

    constructor(data: String, date: Date, read: Boolean, sender: String) {
        this.data = data
        this.date = date
        //this.timestamp = timestamp
        this.read = read
        this.sender = sender
    }


}