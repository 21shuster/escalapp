package es.eoi.proyectofinal.casos_uso

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.modelo.Ubicacion
import es.eoi.proyectofinal.utilidades.DialogoSelectorFecha
import es.eoi.proyectofinal.utilidades.DialogoSelectorHora
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.presentacion.SelectorFragment
import java.text.DateFormat
import java.util.*

class CasosUsoUbicacionFecha(
    private val actividad: FragmentActivity?,
    private val fragment: Fragment?,
    private val ubicaciones: UbicacionesAsinc,
    private val adaptador: AdaptadorUbicacionesFirebase?
) : CasosUsoUbicacion(actividad, fragment, ubicaciones, adaptador), OnTimeSetListener, OnDateSetListener {

    var pos = -1
    var ubicacion: Ubicacion? = null

    fun cambiarHora(pos: Int) {
        ubicacion = SelectorFragment.adaptador?.getItem(pos)
        this.pos = pos
        val dialogo = DialogoSelectorHora()
        dialogo.setOnTimeSetListener(this)
        val args = Bundle()
        args.putLong("fecha", ubicacion!!.fecha)
        dialogo.arguments = args
        dialogo.show(actividad!!.supportFragmentManager, "selectorHora")
    }

    override fun onTimeSet(vista: TimePicker, hora: Int, minuto: Int) {
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = ubicacion!!.fecha
        calendario[Calendar.HOUR_OF_DAY] = hora
        calendario[Calendar.MINUTE] = minuto
        ubicacion?.fecha = calendario.timeInMillis
        actualizaPosLugar(pos, ubicacion)
        var hora = actividad!!.findViewById<View>(R.id.tvHora) as TextView
        hora.text = DateFormat.getTimeInstance().format(
            Date(ubicacion!!.fecha)
        )
    }

    fun cambiarFecha(pos: Int) {
        ubicacion = SelectorFragment.adaptador!!.getItem(pos)
        this.pos = pos
        val dialogo = DialogoSelectorFecha()
        dialogo.setOnDateSetListener(this)
        val args = Bundle()
        args.putLong("fecha", ubicacion!!.fecha)
        dialogo.arguments = args
        dialogo.show(actividad!!.supportFragmentManager, "selectorFecha")
    }

    override fun onDateSet(view: DatePicker, año: Int, mes: Int, dia: Int) {
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = ubicacion!!.fecha
        calendario[Calendar.YEAR] = año
        calendario[Calendar.MONTH] = mes
        calendario[Calendar.DAY_OF_MONTH] = dia
        ubicacion?.fecha = calendario.timeInMillis
        actualizaPosLugar(pos, ubicacion)
        var fecha = actividad!!.findViewById<View>(R.id.tvFecha) as TextView
        fecha.text = DateFormat.getDateInstance().format(
            Date(ubicacion!!.fecha)
        )
    }
}