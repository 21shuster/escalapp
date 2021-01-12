package es.eoi.proyectofinal.adaptador

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.TipoUbicacion
import es.eoi.proyectofinal.modelo.Ubicacion
import es.eoi.proyectofinal.presentacion.SelectorFragment
import java.lang.Math.round
import kotlin.math.cos
import kotlin.math.sin

class AdaptadorUbicaciones(private val baseContext: Context, private val ubicaciones: UbicacionesFirebase) :
    RecyclerView.Adapter<AdaptadorUbicaciones.ViewHolder>(), View.OnClickListener {

    var inflador //Crea Layouts a partir del XML
            : LayoutInflater? = baseContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var mAdapter: FirestoreRecyclerAdapter<Ubicacion, ViewHolder>

    private var mBaseQuery =
        FirebaseFirestore.getInstance().collection(R.string.NODO_UBICACIONES.toString())

    var onClickListener: View.OnClickListener? = null

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nombre: TextView
        var direccion: TextView
        var foto: ImageView
        var valoracion: RatingBar
        var distancia: TextView
        var dificultad: ImageView

        init {
            nombre = itemView.findViewById<View>(R.id.tvNombre) as TextView
            direccion = itemView.findViewById<View>(R.id.tvDireccion) as TextView
            foto = itemView.findViewById<View>(R.id.ivFoto) as ImageView
            valoracion = itemView.findViewById<View>(R.id.rtValoracion) as RatingBar
            distancia = itemView.findViewById<View>(R.id.tvDistancia) as TextView
            dificultad = itemView.findViewById<View>(R.id.ivLevel) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflamos la vista desde el xml
        val v: View = inflador!!.inflate(R.layout.elemento_lista, null)
        v.setOnClickListener(onClickListener)
        return ViewHolder(v)
    }

    // Usando como base el ViewHolder y lo personalizamos
    override fun onBindViewHolder(holder: ViewHolder, posicion: Int) {
        val lugar: Ubicacion = SelectorFragment.adaptador!!.getItem(posicion)
        holder?.let { personalizaVista(it, lugar) }
    }

    // Indicamos el número de elementos de la lista
    override fun getItemCount(): Int {
        return SelectorFragment.adaptador!!.itemCount
    }

    companion object {
        @JvmStatic
        // Personalizamos un ViewHolder a partir de un lugar
        fun personalizaVista(holder: ViewHolder, lugar: Ubicacion) {
            holder.nombre.text = lugar.nombre
            holder.direccion.text = lugar.direccion
            var id: Int = R.drawable.otros
            when (lugar.tipoEnum) {
                TipoUbicacion.OTROS -> id = R.drawable.otros
                TipoUbicacion.PAISAJE -> id = R.drawable.paisaje
                TipoUbicacion.PARQUE -> id = R.drawable.parque
                TipoUbicacion.CAMPING -> id = R.drawable.camping
                TipoUbicacion.CICLISMO -> id = R.drawable.ciclismo
                TipoUbicacion.NATACION -> id = R.drawable.natacion
                TipoUbicacion.ESCALADA -> id = R.drawable.escalada
                TipoUbicacion.SENDERISMO -> id = R.drawable.senderismo
                TipoUbicacion.PESCA -> id = R.drawable.pesca
                TipoUbicacion.NATURALEZA -> id = R.drawable.naturaleza
                TipoUbicacion.GASOLINERA -> id = R.drawable.gasolinera
                TipoUbicacion.ALOJAMIENTO -> id = R.drawable.hotel
            }
            when (lugar.dificultad){
                1 -> {
                    holder.dificultad.setImageResource(R.drawable.easy)
                }
                2 -> {
                    holder.dificultad.setImageResource(R.drawable.middle)
                }
                3 -> {
                    holder.dificultad.setImageResource(R.drawable.expert)
                }
            }
            holder.foto.setImageResource(id)
            holder.foto.scaleType = ImageView.ScaleType.FIT_END
            holder.valoracion.rating = lugar.valoracion
            if (UbicacionesAsinc.posicionActual != null && lugar.posicion != null) {
                val d = UbicacionesAsinc.posicionActual.distancia(lugar.posicion).toString()
                if (d < "2000") {
                    holder.distancia.text = "$d m"
                } else holder.distancia.text = "${round(d.toDouble()) / 1000} Km"
            }
        }

    }

    override fun onClick(p0: View?) {

        val newOptions = FirestoreRecyclerOptions.Builder<Ubicacion>()
            .setQuery(mBaseQuery, Ubicacion::class.java)
            .build()


        // Change options of adapter.
        //mAdapter.updateOptions(newOptions)
    }
}

private fun GeoPoint.distancia(posicion: GeoPoint): Any {
    val RADIO_TIERRA = 6371000.0 // en metros
    val dLat = Math.toRadians(latitude - posicion.latitude)
    val dLon = Math.toRadians(longitude - posicion.longitude)
    val lat1 = Math.toRadians(posicion.latitude)
    val lat2 = Math.toRadians(latitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            sin(dLon / 2) * sin(dLon / 2) *
            cos(lat1) * cos(lat2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return c * RADIO_TIERRA
}



