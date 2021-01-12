package es.eoi.proyectofinal.presentacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.CategoryActivity
import es.eoi.proyectofinal.adaptador.AdaptadorCategorias
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.Category
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private var allCategories: ArrayList<Category> = arrayListOf()
    private var TAG = "myApp"
    lateinit var db: FirebaseFirestore
    lateinit var mAdapter: AdaptadorCategorias

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Categorias"
        mainRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        onInit()
    }

    fun onInit() {
        db = Firebase.firestore
        mAdapter = AdaptadorCategorias(allCategories) {
            val intent = Intent(activity, CategoryActivity::class.java)
            intent.putExtra("cat", it)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
        mainRecyclerView.adapter = mAdapter
        loadAllCategories()
    }

    fun loadAllCategories() {
        db.collection(DataHolder.dbCategories).get().addOnSuccessListener { result ->
            allCategories.clear()
            for (document in result) {
                val cat = document.toObject(Category::class.java)
                Log.v(TAG, "cat : ${cat.toString()}")
                cat.id = document.id
                allCategories.add(cat)
            }
            mAdapter.notifyDataSetChanged()
        }

        .addOnFailureListener { e ->
            Log.v(TAG, "loadAllUbications:failure", e)
        }
    }
}