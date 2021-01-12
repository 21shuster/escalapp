package es.eoi.proyectofinal.actividad

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.eoi.proyectofinal.presentacion.PreferenciasFragment


class PreferenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, PreferenciasFragment())
            .commit()
    }
}