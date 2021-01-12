package es.eoi.proyectofinal.adaptador

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.modelo.Ubicacion


class AdaptadorCategoriasDetail(
    private val mDataSet: List<Ubicacion>?,
    val function: (Ubicacion) -> Unit,
) : RecyclerView.Adapter<AdaptadorCategoriasDetail.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = mDataSet?.get(position)
        data?.let {
            holder.bindItems(it)
            Log.v("myApp", "it: ${it.toString()}")
            holder.itemView.setOnClickListener {
                function(data!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataSet?.size ?: 0
    }

    inner class MainViewHolder(var v: View) : RecyclerView.ViewHolder(v) {

        private val title = itemView.findViewById<View>(R.id.tvTitle) as TextView
        private val rating = itemView.findViewById<View>(R.id.tvRating) as TextView
        private val foto = itemView.findViewById<View>(R.id.ivUbi) as ImageView

        fun bindItems(data: Ubicacion) {
            title.text = data.nombre
            rating.text = data.valoracion.toString()
            Picasso.get().load(data.foto).into(foto)
        }
    }
}