package es.eoi.proyectofinal.modelo

import java.io.Serializable
class Category : Serializable {
    var id: String? = null
    var name: String? = null
    var foto: String? = null
    var text: String? = null

    constructor(id: String, name: String, image: String, text: String) {
        this.id = id
        this.name = name
        this.foto = foto
        this.text = text
    }

    constructor()

    override fun toString(): String {
        return "Category(id=$id, name=$name, foto=$foto, text=$text)"
    }
}