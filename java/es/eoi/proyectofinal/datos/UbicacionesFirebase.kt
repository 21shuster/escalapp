package es.eoi.proyectofinal.datos

import android.util.Log
import com.google.firebase.firestore.*
import es.eoi.proyectofinal.modelo.Ubicacion

class UbicacionesFirebase : UbicacionesAsinc {

    private val nodo: CollectionReference
    private val TAG = "myApp"

    override fun elemento(id: String?, escuchador: UbicacionesAsinc.EscuchadorElemento?) {
        nodo.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                escuchador?.onRespuesta(null)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                val lugar: Ubicacion? = dc.document.toObject(Ubicacion::class.java)
                escuchador?.onRespuesta(lugar)
                when (dc.type) {
                    DocumentChange.Type.ADDED -> Log.d(TAG, "New city: ${dc.document.data}")
                    DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: ${dc.document.data}")
                    DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
                }
            }
        }
    }

    override fun anyade(ubicacion: Ubicacion?) {
        ubicacion?.let { nodo.add(it) }
    }

    override fun nuevo(): String? {
        return nodo.document().set("").toString()
    }

    override fun borrar(id: String?) {
        nodo.document(id!!).delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }

    override fun actualiza(id: String?, lugar: Ubicacion?) {
        lugar?.let { nodo.document(id!!).set(it) }
    }

    override fun tamanyo(escuchador: UbicacionesAsinc.EscuchadorTamanyo?) {
        nodo.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                escuchador?.onRespuesta(-1)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                escuchador?.onRespuesta(snapshots.count().toLong())
            }
        }
    }

    override fun getUbicaciones(escuchador: UbicacionesAsinc.EscuchadorUbicaciones?){
        nodo.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = ArrayList<Ubicacion>()
                    for (document in task.result!!) {
                        val ubi = document.toObject(Ubicacion::class.java)
                        list.add(ubi)
                    }
                    escuchador?.onRespuesta(list)
                }
            }
    }

    override fun filter(cadena: String?, escuchador: UbicacionesAsinc.EscuchadorUbicaciones?) {
        nodo.whereArrayContains("nombre", cadena!!)
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list = ArrayList<Ubicacion>()
                for (document in task.result!!) {
                    val ubi = document.toObject(Ubicacion::class.java)
                    list.add(ubi)
                }
                escuchador?.onRespuesta(list)
            }
        }
    }

    companion object {
        private const val NODO_UBICACIONES = "ubicaciones"
    }

    init {
        val database = FirebaseFirestore.getInstance()
        nodo = database.collection(NODO_UBICACIONES)
    }
}