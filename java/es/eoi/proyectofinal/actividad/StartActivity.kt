package es.eoi.proyectofinal.actividad

import android.content.Intent
import es.eoi.proyectofinal.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*
import render.animations.Attention
import render.animations.Fade
import render.animations.Render
import render.animations.Zoom
import java.lang.StringBuilder
import java.util.*


class StartActivity : AppCompatActivity() {

    private val render = Render(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        appear()
        tada()
        disappear()
        nextActivity()
    }

    fun appear() {
        render.setAnimation(Zoom().In(logo))
        render.setDuration(1500)
        viewGradient.visibility = View.VISIBLE
        render.start()
    }

    fun disappear() {
        Handler(Looper.getMainLooper()).postDelayed({
            render.setAnimation(Fade().Out(logo))
            render.setDuration(1500)
            render.start()
        }, 3500)
    }

    fun tada() {
        Handler(Looper.getMainLooper()).postDelayed({
            render.setAnimation(Attention().Swing(logo))
            render.setDuration(700)
            render.start()
        }, 1000)
    }

    fun nextActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3500)
    }
}