package es.eoi.proyectofinal.actividad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.adaptador.ViewPagerAdapter
import es.eoi.proyectofinal.modelo.User
import es.eoi.proyectofinal.presentacion.LoginFragment
import es.eoi.proyectofinal.presentacion.RegisterFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 1
    val TAG = "miapp"
    lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val myAdapter = supportFragmentManager?.let { ViewPagerAdapter(it) }
        myAdapter?.addFragment(LoginFragment(), "LOGIN")
        myAdapter?.addFragment(RegisterFragment(), "REGISTRO")
        // Instanciamos el ViewPager con el Adapter creado
        mainViewPager.adapter = myAdapter
        // Asignamos el viewPager al TabLayout para que cambie cuando hacemos swipe
        mainTabLayout.setupWithViewPager(mainViewPager)
    }
}