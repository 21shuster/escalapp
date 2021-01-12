package es.eoi.proyectofinal.modelo

import java.io.Serializable

class User : Serializable{
    var id : String? = null
    var nombre: String? = null
    var apellido: String? = null
    var email: String? = null
    var phone: String? = null
    var photo: String? = null

    constructor(){}

    override fun toString(): String {
        return "User(id=$id, nombre=$nombre, apellido=$apellido, email=$email, phone=$phone, photo=$photo)"
    }

}