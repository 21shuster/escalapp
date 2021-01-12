package es.eoi.proyectofinal.presentacion

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import es.eoi.proyectofinal.R


class PreferenciasFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreate(savedInstanceState)
        setPreferencesFromResource(R.xml.preferencias, rootKey)
    }
}
