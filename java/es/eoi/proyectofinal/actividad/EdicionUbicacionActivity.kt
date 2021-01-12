package es.eoi.proyectofinal.actividad

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.casos_uso.CasosUsoLocalizacion
import es.eoi.proyectofinal.casos_uso.CasosUsoUbicacion
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.TipoUbicacion
import es.eoi.proyectofinal.modelo.Ubicacion
import es.eoi.proyectofinal.presentacion.SelectorFragment
import kotlinx.android.synthetic.main.activity_edicion_ubicacion.*


class EdicionUbicacionActivity : AppCompatActivity() {

    val TAG = "miapp"
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    private lateinit var ubicaciones: UbicacionesAsinc
    private lateinit var casoUso: CasosUsoUbicacion
    private lateinit var casoUsoLoc: CasosUsoLocalizacion
    private var adaptador: AdaptadorUbicacionesFirebase? = null

    private var pos = 0
    private var _id: String? = null
    private var edit = false
    private var dificulty: Int = 2
    private var ubicacion: Ubicacion? = Ubicacion()
    private var geoPos: GeoPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edicion_ubicacion)
        auth = Firebase.auth
        db = Firebase.firestore
        ubicaciones = UbicacionesFirebase()
        casoUso = CasosUsoUbicacion(this, null, ubicaciones as UbicacionesFirebase, adaptador)
        casoUsoLoc = CasosUsoLocalizacion(this, 1)

        val adaptador: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, TipoUbicacion.nombres
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipo.adapter = adaptador

        val extras = intent.extras
        pos = extras!!.getInt("pos", -1)
        _id = extras.getString("_id", null)
        geoPos = GeoPoint(extras.getDouble("lon"), extras.getDouble("lat"))
        if (geoPos == null){
            casoUsoLoc.ultimaLocalizazion()
            geoPos = casoUsoLoc.posicionActual
        }
        if (pos != -1) actualizaVistas()
        setEditDificulty(2)

        btnEasy.setOnClickListener {
            setDificulty(it as MaterialButton)
            setEditDificulty(1)
        }

        btnMiddle.setOnClickListener {
            setDificulty(it as MaterialButton)
            setEditDificulty(2)
        }

        btnExpert.setOnClickListener {
            setDificulty(it as MaterialButton)
            setEditDificulty(3)
        }

        btn_crearUbi.setOnClickListener{
            createNewUbication()
        }
    }

    fun actualizaVistas() {
        edit = true
        ubicacion = if (DataHolder.currentUbication != null)
            DataHolder.currentUbication
        else SelectorFragment.adaptador!!.getItem(pos)
        ubicacion?.let {
            edNombre.setText(it.nombre.toString())
            edDireccion.setText(it.direccion.toString())
            edTelefono.setText(it.telefono.toString())
            edUrl.setText(it.url.toString())
            edComentario.setText(it.comentario.toString())
        }
        spTipo.setSelection(ubicacion!!.tipoEnum!!.ordinal)
        btn_crearUbi.text = "modificar ubicación"
        ubicacion?.dificultad?.let { setEditDificulty(it) }
    }

    fun setEditDificulty(mPriorioty: Int) {
        resetButtons()

        if (mPriorioty == 1) {
            btnEasy.alpha = 1F
        }

        if (mPriorioty == 2) {
            btnMiddle.alpha = 1F
        }

        if (mPriorioty == 3) {
            btnExpert.alpha = 1F
        }
    }

    fun setDificulty(btn: MaterialButton) {
        resetButtons()

        if (btn.tag == 0) {
            dificulty = 0
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light))
        }

        if (btn.tag == 1) {
            dificulty = 1
            btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
        }

        if (btn.tag == 2) {
            dificulty = 2
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError))
        }
    }

    fun resetButtons() {
        btnEasy.alpha = 0.5F
        btnMiddle.alpha = 0.5F
        btnExpert.alpha = 0.5F
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edicion_ubicacion, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.accion_guardar -> {
                ubicacion!!.apply {
                    nombre = edNombre.text.toString()
                    tipoEnum = TipoUbicacion.values().get(spTipo!!.selectedItemPosition)
                    direccion = edDireccion.text.toString()
                    telefono = edTelefono.text.toString().toInt()
                    dificultad = dificulty
                    url = edUrl.text.toString()
                    comentario = edComentario.text.toString()
                    posicion = geoPos!!
                }
                DataHolder.currentUbication = ubicacion
                casoUso.guardar(_id, ubicacion)
                finish()
                true
            }
            R.id.accion_cancelar -> {
                if (intent.extras!!.getBoolean("nuevo", false)) {
                    ubicaciones.borrar(_id)
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun createNewUbication() {
        //val uid = auth.currentUser!!.uid

        val newUbi = Ubicacion()
        newUbi.nombre = edNombre.text.toString()
        newUbi.direccion = edDireccion.text.toString()
        newUbi.comentario = edComentario.text.toString()
        newUbi.telefono = edTelefono.text.toString().toInt()
        newUbi.tipoEnum = TipoUbicacion.get(spTipo.selectedItem.toString())[0]
        newUbi.url = edUrl.text.toString()
        newUbi.fecha =  System.currentTimeMillis()
        newUbi.dificultad = dificulty
        newUbi.posicion = geoPos!!

        if (edit) {
            db.collection(DataHolder.dbUbication)
                .document(ubicacion?.id!!).set(newUbi)
                .addOnSuccessListener {
                    Log.v(TAG, "createNewTask:TODOFENOMENAL")
                    Toast.makeText(this, "Ubicacion actualizada correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.v(TAG, "createNewTask:failure", e)
                    Toast.makeText(this, "Error al crear la ubicacion: ${e.message}", Toast.LENGTH_LONG)
                }
        } else {
            db.collection(DataHolder.dbUbication)
                .add(newUbi)
                .addOnSuccessListener {
                    Log.v(TAG, "createNewTask:TODOFENOMENAL")
                    Toast.makeText(this, "Ubicacion creada correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.v(TAG, "createNewTask:failure", e)
                    Toast.makeText(this, "Error al crear la ubicacion: ${e.message}", Toast.LENGTH_LONG)
                }
        }
    }
}
