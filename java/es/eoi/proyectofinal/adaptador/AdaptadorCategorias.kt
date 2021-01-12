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
import es.eoi.proyectofinal.modelo.Category

class AdaptadorCategorias(
    private val mDataSet: List<Category>?,
    val function: (Category) -> Unit,
) : RecyclerView.Adapter<AdaptadorCategorias.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
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

        private val name = itemView.findViewById<View>(R.id.tvNameCategory) as TextView
        private val foto = itemView.findViewById<View>(R.id.ivSearch) as ImageView
        private val menu = itemView.findViewById<View>(R.id.ivMenu) as ImageView

        fun bindItems(data: Category) {
            name.text = data.name
            Picasso.get().load(data.foto).into(foto)
//            menu.setOnClickListener {
//                val popupMenu: PopupMenu = PopupMenu(menu.context, menu)
//                popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
//                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
//                    when (item.itemId) {
//                        R.id.accion_add ->
//                            Toast.makeText(v.ivMenu.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
//                                .show()
//                        R.id.accion_delete ->
//                            Toast.makeText(v.ivMenu.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
//                                .show()
//                    }
//                    true
//                })
//                popupMenu.show()

        }
    }
}