package es.eoi.proyectofinal.datos


import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.modelo.Ubicacion


interface UbicacionesAsinc {
    interface EscuchadorElemento {
        fun onRespuesta(lugar: Ubicacion?)
    }

    interface EscuchadorTamanyo {
        fun onRespuesta(tamanyo: Long)
    }

    interface EscuchadorUbicaciones {
        fun onRespuesta(value: ArrayList<Ubicacion>)
    }

    fun elemento(id: String?, escuchador: EscuchadorElemento?)
    fun anyade(lugar: Ubicacion?)
    fun nuevo(): String?
    fun borrar(id: String?)
    fun actualiza(id: String?, lugar: Ubicacion?)
    fun tamanyo(escuchador: EscuchadorTamanyo?)
    fun getUbicaciones(escuchador: EscuchadorUbicaciones?)
    fun filter(cadena: String?, escuchador: EscuchadorUbicaciones?)

    companion object {
        val posicionActual: GeoPoint = GeoPoint(0.0, 0.0)
    }
}

