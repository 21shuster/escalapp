package es.eoi.proyectofinal.modelo

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class Ubicacion : Serializable{
    var id: String? = null
    var nombre: String? = null
    var direccion: String? = null
    var posicion: GeoPoint
    var tipoEnum: TipoUbicacion?
    var foto: String? = null
    var telefono = 0
    var url: String? = null
    var comentario: String? = null
    var fecha: Long
    var valoracion = 0f
    var dificultad = 0

    constructor(
        nombre: String?, direccion: String?, longitud: Double,
        latitud: Double, tipo: TipoUbicacion?, telefono: Int, url: String?, comentario: String?,
        valoracion: Int, dificultad: Int
    ) {
        fecha = System.currentTimeMillis()
        posicion = GeoPoint(longitud, latitud)
        this.nombre = nombre
        this.direccion = direccion
        tipoEnum = tipo
        this.telefono = telefono
        this.url = url
        this.comentario = comentario
        this.valoracion = valoracion.toFloat()
        this.dificultad = dificultad
    }

    constructor() {
        fecha = System.currentTimeMillis()
        posicion = GeoPoint(0.0, 0.0)
        tipoEnum = TipoUbicacion.OTROS
    }

    fun getTipo(): String? {
        return if (tipoEnum == null) null else tipoEnum!!.name
    }

    fun setTipo(nombre: String?) {
        if (nombre == null) tipoEnum = null else tipoEnum = TipoUbicacion.valueOf(nombre)
    }

    override fun toString(): String {
        return "Ubicacion{nombre='$nombre', " +
                "direccion='$direccion', " +
                "posicion=$posicion, " +
                "tipo=$tipoEnum, " +
                "foto='$foto', " +
                "telefono=$telefono, " +
                "url='$url', " +
                "comentario='$comentario', " +
                "fecha=$fecha, " +
                "valoracion=$valoracion, " +
                "dificultad=$dificultad,  }"
    }

}
