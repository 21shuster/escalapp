package es.eoi.proyectofinal.actividad

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.casos_uso.CasosUsoActividades
import es.eoi.proyectofinal.casos_uso.CasosUsoLocalizacion
import es.eoi.proyectofinal.casos_uso.CasosUsoUbicacion
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.ChatUser
import es.eoi.proyectofinal.modelo.Message
import es.eoi.proyectofinal.presentacion.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    //private RecyclerView recyclerView;
    private var ubicaciones: UbicacionesAsinc? = null
    private var casoUso: CasosUsoUbicacion? = null
    private var usoActividades: CasosUsoActividades? = null
    private var usoLocalizacion: CasosUsoLocalizacion? = null
    private var user: FirebaseUser? = null
    private var TAG: String = "miapp"

    var adaptador: AdaptadorUbicacionesFirebase? = null
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var uid: String
    var pendingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        )

        auth = Firebase.auth
        db = Firebase.firestore
        uid = auth.currentUser!!.uid
        ubicaciones = UbicacionesFirebase()
        casoUso = CasosUsoUbicacion(this, null, ubicaciones as UbicacionesFirebase, adaptador)
        usoActividades = CasosUsoActividades(this)
        usoLocalizacion = CasosUsoLocalizacion(this, SOLICITUD_PERMISO_LOCALIZACION)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        updateUI()
        logoXmarks!!.setOnClickListener {
            goToFragment(ProfileFragment())
        }
        selectFragment()

        // val accessToken: AccessToken = AccessToken.getCurrentAccessToken()
        // val isLoggedIn = accessToken != null && !accessToken.isExpired

    }

    private fun selectFragment(){
        bottom_navigation_view.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    toolbar_layout.title = "Ubicaciones"
                    bottom_navigation_view.itemIconTintList = ContextCompat.getColorStateList(
                        this,
                        R.color.color_selected
                    )
                    bottom_navigation_view.itemBackground = ContextCompat.getDrawable(
                        this,
                        R.drawable.checked
                    )
                    goToFragment(SelectorFragment())
                    app_bar.setExpanded(true)
                    true
                }
                R.id.ubication -> {
                    toolbar_layout.title = "Map"
                    bottom_navigation_view.itemIconTintList = ContextCompat.getColorStateList(
                        this,
                        R.color.color_selected
                    )
                    bottom_navigation_view.itemBackground = ContextCompat.getDrawable(
                        this,
                        R.drawable.checked
                    )
                    goToFragment(MapsFragment())
                    app_bar.setExpanded(false)
                    true
                }
                R.id.buscar -> {
                    toolbar_layout.title = "Tu zona"
                    bottom_navigation_view.itemIconTintList = ContextCompat.getColorStateList(
                        this,
                        R.color.color_selected
                    )
                    bottom_navigation_view.itemBackground = ContextCompat.getDrawable(
                        this,
                        R.drawable.checked
                    )
                    goToFragment(SearchFragment())
                    app_bar.setExpanded(false)
                    true
                }
                R.id.notificaciones -> {
                    toolbar_layout.title = "Notificaciones"
                    bottom_navigation_view.itemIconTintList = ContextCompat.getColorStateList(
                        this,
                        R.color.color_selected
                    )
                    bottom_navigation_view.itemBackground = ContextCompat.getDrawable(
                        this,
                        R.drawable.checked
                    )
                    goToFragment(NotificationFragment())
                    app_bar.setExpanded(false)
                    true
                }
                R.id.perfil -> {
                    toolbar_layout.title = "Perfil"
                    bottom_navigation_view.itemIconTintList = ContextCompat.getColorStateList(
                        this,
                        R.color.color_selected
                    )
                    bottom_navigation_view.itemBackground = ContextCompat.getDrawable(
                        this,
                        R.drawable.checked
                    )
                    goToFragment(ProfileFragment())
                    app_bar.setExpanded(false)
                    true
                }
                else -> false
            }
        }
        bottom_navigation_view.selectedItemId = R.id.home
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        user = auth.currentUser
        updateUI()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            usoActividades!!.lanzarPreferencias(RESULTADO_PREFERENCIAS)
            return true
        }
        if (id == R.id.acercaDe) {
            usoActividades!!.lanzarAcercaDe()
            return true
        }
        if(item.itemId == R.id.logout) {
            Firebase.auth.signOut()
            LoginManager.getInstance().logOut()
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
        if (id == R.id.menu_add) {
            val i = Intent(this@MainActivity, EdicionUbicacionActivity::class.java)
            i.putExtra("_id", "")
            i.putExtra("pos", -1)
            startActivity(i)
        }
        if (id == R.id.salir) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) usoLocalizacion?.permisoConcedido()
        //recyclerView.invalidate();
    }

    private fun updateUI() {
        var mIcon_val: Bitmap? = null
        if (user != null) {
            try {
                val newurl = URL(user!!.photoUrl.toString())
                Log.e("miapp", "Uri: $newurl User: ${auth.currentUser?.uid!!}")
                mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
                logoXmarks.setImageBitmap(mIcon_val)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getChatUser(idUser: String){
        db.collection(DataHolder.dbUsers).document(idUser)
            .collection(DataHolder.CHATS).addSnapshotListener { value, e ->
                for (doc in value!!) {
                    val chat = doc.toObject(ChatUser::class.java)
                    getPendingCount(chat.idChat.toString())
                }
            }
    }

    fun getPendingCount(idChat: String){
        db.collection(DataHolder.DBCHATS).document(idChat)
            .collection(DataHolder.MESSAGES).addSnapshotListener{ value, e ->
                for (doc in value!!){
                    val message = doc.toObject(Message::class.java)
                    if(!message.read && message.sender != uid) pendingCount ++
                }
                if (pendingCount > 0) {
                    var badge = bottom_navigation_view.getOrCreateBadge(R.id.notificaciones)
                    badge.isVisible = true
                    // An icon only badge will be displayed unless a number is set:
                    badge.number = pendingCount
                } else bottom_navigation_view.removeBadge(R.id.notificaciones)
            }
    }

    override fun onResume() {
        super.onResume()
        usoLocalizacion!!.activar()
        updateUI()
        pendingCount = 0
        getChatUser(uid)
    }

    override fun onPause() {
        super.onPause()
        usoLocalizacion!!.desactivar()
    }

    companion object {
        private const val SOLICITUD_PERMISO_LOCALIZACION = 1
        const val RESULTADO_PREFERENCIAS = 0
        private const val RC_SIGN_IN = 1
        private const val RC_SIGN_OUT = 2
        private var mAuth : FirebaseAuth? = null
        private var mGoogleSignInClient: GoogleSignInClient? = null
    }
}
