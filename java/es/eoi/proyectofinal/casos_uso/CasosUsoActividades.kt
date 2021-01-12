package es.eoi.proyectofinal.casos_uso


import android.app.Activity
import android.content.Intent
import es.eoi.proyectofinal.actividad.AcercaDeActivity
import es.eoi.proyectofinal.actividad.PreferenciasActivity


class CasosUsoActividades(protected var actividad: Activity) {
    fun lanzarAcercaDe() {
        actividad.startActivity(
            Intent(actividad, AcercaDeActivity::class.java)
        )
    }

    fun lanzarPreferencias(codidoSolicitud: Int) {
        actividad.startActivityForResult(
            Intent(actividad, PreferenciasActivity::class.java), codidoSolicitud
        )
    }

}