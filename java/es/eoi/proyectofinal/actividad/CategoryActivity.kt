package es.eoi.proyectofinal.actividad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.adaptador.AdaptadorCategoriasDetail
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.Category
import es.eoi.proyectofinal.modelo.Ubicacion
import kotlinx.android.synthetic.main.activity_category.*

class CategoryActivity : AppCompatActivity() {

    private var category: Category? = null
    private var allUbications: ArrayList<Ubicacion> = arrayListOf()
    private var TAG = "myApp"
    lateinit var db: FirebaseFirestore
    lateinit var mAdapter: AdaptadorCategoriasDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val extras = intent.extras
        category = extras!!.get("cat") as Category
        toolbarItem.title = category!!.name
        tvDescripcion.text = category!!.text.toString().replace("\\n", "\n")
        Picasso.get().load(category!!.foto).into(ivCategory)
        recyclerCategory!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL ,false)
        onInit()
    }

    fun onInit() {
        db = Firebase.firestore
        mAdapter = AdaptadorCategoriasDetail(allUbications){ ubi ->
            DataHolder.currentUbication = ubi
            startActivity(Intent(this, VistaUbicacionActivity::class.java))
        }
        recyclerCategory.adapter = mAdapter
        loadAllUbications()

    }

    fun loadAllUbications(){
        db.collection(DataHolder.dbUbication).get().addOnSuccessListener { result ->
            allUbications.clear()
            for (document in result) {
                val ubi = document.toObject(Ubicacion::class.java)
                Log.v(TAG, "ubi : ${ubi.toString()}")
                ubi.id = document.id
                allUbications.add(ubi)
            }
            mAdapter.notifyDataSetChanged()
        }
            .addOnFailureListener { e ->
                Log.v(TAG, "loadAllUbications:failure", e)
            }
    }

}