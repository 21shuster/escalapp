package es.eoi.proyectofinal.utilidades

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*


class DialogoSelectorFecha : DialogFragment() {
    private var escuchador: OnDateSetListener? = null
    fun setOnDateSetListener(escuchador: OnDateSetListener?) {
        this.escuchador = escuchador
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario = Calendar.getInstance()
        val args = this.arguments
        if (args != null) {
            val fecha = args.getLong("fecha")
            calendario.timeInMillis = fecha
        }
        val año = calendario[Calendar.YEAR]
        val mes = calendario[Calendar.MONTH]
        val dia = calendario[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(activity!!, escuchador, año, mes, dia)
    }
}
