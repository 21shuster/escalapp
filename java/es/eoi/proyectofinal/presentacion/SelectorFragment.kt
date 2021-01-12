package es.eoi.proyectofinal.presentacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.VistaUbicacionActivity
import es.eoi.proyectofinal.adaptador.AdaptadorSelector
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.casos_uso.CasosUsoUbicacion
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.Ubicacion
import kotlinx.android.synthetic.main.fragment_selector.*

class SelectorFragment : Fragment() {
    private var ubicaciones: UbicacionesAsinc? = null
    private var casoUso: CasosUsoUbicacion? = null
    private var recyclerView: RecyclerView? = null
    private var allUbications: ArrayList<Ubicacion> = arrayListOf()
    private var listFilter: ArrayList<Ubicacion> = arrayListOf()
    private var listCategories: ArrayList<String> = arrayListOf()
    private var TAG = "myApp"
    lateinit var db: FirebaseFirestore
    lateinit var mAdapter: AdaptadorSelector

    override fun onCreateView(
        inflador: LayoutInflater, contenedor: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val vista: View = inflador.inflate(
            R.layout.fragment_selector,
            contenedor, false
        )
        recyclerView = vista.findViewById(R.id.recyclerView)
        return vista
    }

    override fun onActivityCreated(state: Bundle?) {
        super.onActivityCreated(state)
        ubicaciones = UbicacionesFirebase()
        casoUso = CasosUsoUbicacion(activity, this, ubicaciones as UbicacionesFirebase, adaptador)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        loadAdapter()
        content.visibility = View.GONE
        imFilter.setOnClickListener(View.OnClickListener { v ->
            crossfade()
        })
    }

    private fun crossfade() {
        content.apply {
            alpha = 0f
            visibility = if(visibility == View.VISIBLE)
                View.GONE
            else View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
        }

        val vistas = content!!.touchables
        val it: Iterator<View> = vistas.iterator()
        while (it.hasNext()) {
            val v = it.next()
            if (v is CardView) v.setOnClickListener{selectCategory(v)}
        }
    }

    fun onInit() {
        db = Firebase.firestore
        loadAllUbications()
        mAdapter = AdaptadorSelector(listFilter){ ubi ->
            DataHolder.currentUbication = ubi
            startActivity(Intent(context, VistaUbicacionActivity::class.java))
        }
        etBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { search(query) }
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { search(query) }
                return true
            }
        })
    }

    fun search(queryText: String) {
        listFilter = allUbications.filter {it.nombre.toString().toLowerCase()!!.contains(queryText.toLowerCase())} as ArrayList<Ubicacion>
        mAdapter = AdaptadorSelector(listFilter){ ubi ->
            DataHolder.currentUbication = ubi
            startActivity(Intent(context, VistaUbicacionActivity::class.java))
        }
        recyclerView!!.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
    }

    fun selectCategory(view: View){
        if(view.alpha == 1F){
            view.alpha = 0.5F
            listCategories.remove(view.tag.toString())
            searchCategory()
        } else {
            view.alpha = 1F
            listCategories.add(view.tag.toString())
            searchCategory()
        }
        Log.v(TAG, "selected items ${listCategories.toString()}")
    }

    fun searchCategory() {
        listFilter = if (listCategories.size == 0) allUbications
        else allUbications.filter {it.tipoEnum.toString() in listCategories} as ArrayList<Ubicacion>
        mAdapter = AdaptadorSelector(listFilter){ ubi ->
            DataHolder.currentUbication = ubi
            startActivity(Intent(context, VistaUbicacionActivity::class.java))
        }
        recyclerView!!.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
    }

    fun loadAllUbications(){
        db.collection(DataHolder.dbUbication).get().addOnSuccessListener { result ->
                allUbications.clear()
                for (document in result) {
                    val ubi = document.toObject(Ubicacion::class.java)
                    ubi.id = document.id
                    allUbications.add(ubi)
                }
            }
            .addOnFailureListener { e ->
                Log.v("miapp", "loadAllUbications:failure", e)
            }
    }

    fun loadAdapter(){
        val query = FirebaseFirestore.getInstance()
            .collection("ubicaciones")
            .limit(50)
        val opciones: FirestoreRecyclerOptions<Ubicacion> =
            FirestoreRecyclerOptions.Builder<Ubicacion>()
                .setQuery(query, Ubicacion::class.java)
                .build()
        onInit()
        adaptador = AdaptadorUbicacionesFirebase(opciones)
        DataHolder.currentUbication = null
        adaptador!!.setOnItemClickListener{
            casoUso!!.mostrar(recyclerView!!.getChildAdapterPosition(it))
        }
        //DataHolder.adaptadorUbi = AdaptadorUbicacionesFirebase(opciones)
        recyclerView!!.adapter = adaptador
        adaptador!!.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adaptador?.stopListening()
    }


    companion object {
        var adaptador: AdaptadorUbicacionesFirebase? = null
    }
}
