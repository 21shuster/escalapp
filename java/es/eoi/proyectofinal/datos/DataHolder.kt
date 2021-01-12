package es.eoi.proyectofinal.datos
import es.eoi.proyectofinal.modelo.ChatUser
import es.eoi.proyectofinal.modelo.Ubicacion
import es.eoi.proyectofinal.modelo.User

object DataHolder {

    var currentChatUser: User? = null
    var currentChat: ChatUser? = null

    val dbUsers = "users"
    val dbUbication = "ubicaciones"
    val dbCategories = "categorias"
    var currentUbication: Ubicacion? = null

    val DBUSERS = "users"
    val DBCHATS = "db_chats"
    val CHATS = "chats"
    val MESSAGES = "messages"

}