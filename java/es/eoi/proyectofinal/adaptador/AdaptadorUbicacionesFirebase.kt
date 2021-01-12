package es.eoi.proyectofinal.adaptador

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.modelo.Ubicacion

class AdaptadorUbicacionesFirebase(
    opciones: FirestoreRecyclerOptions<Ubicacion>
) : FirestoreRecyclerAdapter<Ubicacion, AdaptadorUbicaciones.ViewHolder>(opciones) {

    protected var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AdaptadorUbicaciones.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.elemento_lista, parent, false)
        return AdaptadorUbicaciones.ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AdaptadorUbicaciones.ViewHolder,
        position: Int,
        ubicacion: Ubicacion
    ) {
        AdaptadorUbicaciones.personalizaVista(holder, ubicacion)
        holder.itemView.setOnClickListener(onClickListener)
    }

    fun setOnItemClickListener(onClick: View.OnClickListener?) {
        onClickListener = onClick
    }

    fun getKey(pos: Int): String {
        return super.getSnapshots().getSnapshot(pos).id
    }

}
