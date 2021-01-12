package es.eoi.proyectofinal.utilidades

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*


class DialogoSelectorHora : DialogFragment() {
    private var escuchador: OnTimeSetListener? = null
    fun setOnTimeSetListener(escuchador: OnTimeSetListener?) {
        this.escuchador = escuchador
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario = Calendar.getInstance()
        val args = this.arguments
        if (args != null) {
            val fecha = args.getLong("fecha")
            calendario.timeInMillis = fecha
        }
        val hora = calendario[Calendar.HOUR_OF_DAY]
        val minuto = calendario[Calendar.MINUTE]
        return TimePickerDialog(
            activity, escuchador, hora,
            minuto, DateFormat.is24HourFormat(activity)
        )
    }
}
