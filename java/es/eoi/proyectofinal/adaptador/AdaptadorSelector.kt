package es.eoi.proyectofinal.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.modelo.TipoUbicacion
import es.eoi.proyectofinal.modelo.Ubicacion
import kotlin.math.cos
import kotlin.math.sin

class AdaptadorSelector(private val mDataSet: List<Ubicacion>, val onClick: (Ubicacion) -> Unit) :
    RecyclerView.Adapter<AdaptadorSelector.MainViewHolder>() {

    var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.elemento_lista, parent, false)
        v.setOnClickListener(onClickListener)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = mDataSet[position]
        holder.itemView.setOnClickListener(onClickListener)
        data.let {
            holder.assignData(it)
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun setOnItemClickListener(onClick: View.OnClickListener?) {
        onClickListener = onClick
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val nombre = itemView.findViewById<View>(R.id.tvNombre) as TextView
        private val direccion = itemView.findViewById<View>(R.id.tvDireccion) as TextView
        private val foto = itemView.findViewById<View>(R.id.ivFoto) as ImageView
        private val valoracion = itemView.findViewById<View>(R.id.rtValoracion) as RatingBar
        private val distancia = itemView.findViewById<View>(R.id.tvDistancia) as TextView
        private val dificultad = itemView.findViewById<View>(R.id.ivLevel) as ImageView
        private val card = itemView.findViewById<View>(R.id.card) as CardView

        fun assignData(data: Ubicacion) {
            nombre.text = data.nombre
            direccion.text = data.direccion
            var id: Int = R.drawable.otros
            when (data.tipoEnum) {
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
            when (data.dificultad) {
                1 -> dificultad.setImageResource(R.drawable.easy)
                2 -> dificultad.setImageResource(R.drawable.middle)
                3 -> dificultad.setImageResource(R.drawable.expert)
            }
            foto.setImageResource(id)
            foto.scaleType = ImageView.ScaleType.FIT_END
            valoracion.rating = data.valoracion
            card.setOnClickListener {
                onClick(data)
            }
            if (UbicacionesAsinc.posicionActual != null && data.posicion != null) {
                val d = UbicacionesAsinc.posicionActual.distancia(data.posicion).toString()
                if (d < "2000") {
                    distancia.text = "$d m"
                } else distancia.text = "${Math.round(d.toDouble()) / 1000} Km"
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
    }
}
