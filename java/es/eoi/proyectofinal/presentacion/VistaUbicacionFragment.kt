package es.eoi.proyectofinal.presentacion

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.UbicacionPhotoActivity
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.casos_uso.CasosUsoUbicacionFecha
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.Ubicacion
import kotlinx.android.synthetic.main.vista_ubicacion.*
import kotlinx.android.synthetic.main.vista_ubicacion.view.*
import java.text.DateFormat
import java.util.*

class VistaUbicacionFragment : Fragment() {

    private var foto: ImageView? = null
    private var uriUltimaFoto: Uri? = null
    private var ubicaciones: UbicacionesAsinc? = null
    private val adaptador: AdaptadorUbicacionesFirebase? = null
    private lateinit var db: FirebaseFirestore
    private var casoUso: CasosUsoUbicacionFecha? = null

    /*private*/
    var key = ""
    var TAG = "myApp"
    var pos = 0
    var _id: String? = ""
    private var ubicacion: Ubicacion? = null
    private var v: View? = null

    override fun onCreateView(
        inflador: LayoutInflater,
        contenedor: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        return inflador.inflate(R.layout.vista_ubicacion, contenedor, false)
    }

    override fun onActivityCreated(state: Bundle?) {
        super.onActivityCreated(state)
        ubicaciones = UbicacionesFirebase()
        casoUso = CasosUsoUbicacionFecha(
            activity, this,
            ubicaciones as UbicacionesFirebase, adaptador
        )
        v = view
        v!!.barra_mapa.setOnClickListener {
            ubicacion?.let { it ->
                casoUso!!.verMapa(it)
            }
        }
        v!!.barra_url.setOnClickListener {
            ubicacion?.let { it ->
                casoUso!!.verPgWeb(it)
            }
        }
        v!!.barra_telefono.setOnClickListener {
            ubicacion?.let { it -> casoUso!!.llamarTelefono(it) }
        }
        v!!.camara.setOnClickListener { uriUltimaFoto = casoUso!!.tomarFoto(RESULTADO_FOTO) }
        v!!.galeria.setOnClickListener {
            startActivityForResult(Intent(context, UbicacionPhotoActivity::class.java), Activity.RESULT_OK)
        }
        v!!.eliminar_foto.setOnClickListener { casoUso!!.ponerFoto(pos, "", ivFoto) }
        v!!.icono_hora.setOnClickListener { casoUso!!.cambiarHora(pos) }
        v!!.tvHora.setOnClickListener { casoUso!!.cambiarHora(pos) }
        v!!.icono_fecha.setOnClickListener { casoUso!!.cambiarFecha(pos) }
        v!!.tvFecha.setOnClickListener { casoUso!!.cambiarFecha(pos) }
        val extras = requireActivity().intent.extras
        pos = extras?.getInt("pos", 0) ?: 0
        key = extras?.getString("id") ?: ""
        //_id = getKey(pos)
        Log.d(TAG, "$key")
        ubicacion = if (DataHolder.currentUbication != null)
            DataHolder.currentUbication
        else SelectorFragment.adaptador!!.getItem(pos)!!

        ubicacion?.let { actualizaVistas(it) }
    }

    fun actualizaVistas(ubi: Ubicacion) {
        //v = getView();
        if (ubi != null) {
            (activity as AppCompatActivity).supportActionBar?.title = ubi!!.nombre
            ubi!!.tipoEnum?.recurso?.let { dTipo.setIconResource(it) }
            dTipo.text = ubi!!.tipoEnum?.texto
            tvDireccion.text = ubi!!.direccion
            ubi!!.tipoEnum?.texto?.let { setTipoUbi(it) }
            if (ubi!!.telefono === 0) {
                v!!.tvTelefono.visibility = View.GONE
            } else {
                v!!.tvTelefono.visibility = View.VISIBLE
                val telefono = v!!.tvTelefono
                telefono.text = ubi!!.telefono.toString()
            }
            tvUrl.text = ubi!!.url
            tvComentario.text = ubi!!.comentario
            tvFecha.text = DateFormat.getDateInstance().format(Date(ubi!!.fecha))
            tvHora.text = DateFormat.getTimeInstance().format(Date(ubi!!.fecha))
            rtValoracion.onRatingBarChangeListener = null //<<<<<<<<<<<<<<<<<<
            rtValoracion.rating = ubi!!.valoracion
            rtValoracion.onRatingBarChangeListener =
                OnRatingBarChangeListener { ratingBar, valor, fromUser ->
                    ubi!!.valoracion = valor
                    casoUso?.actualizaPosLugar(pos, ubi)
                    _id = SelectorFragment.adaptador!!.getKey(pos)
                }
            Picasso.get().load(ubi.foto).into(ivFoto)
            setDificulty(ubi.dificultad)
            foto?.let { casoUso?.visualizarFoto(ubi!!, it) }
        }
    }

    private fun setTipoUbi(tipo: String) {
        when(tipo){
            "Ciclismo" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Ciclismo))
            "Otros" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Otros))
            "Paisaje" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Paisaje))
            "Parque" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Parque))
            "Camping" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Camping))
            "Natacion" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Natacion))
            "Escalada" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Escalada))
            "Senderismo" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Senderismo))
            "Pesca" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Pesca))
            "Naturaleza" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Naturaleza))
            "Gasolinera" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Gasolinera))
            "Alojamiento" -> dTipo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Alojamiento))
        }
    }

    fun setDificulty(dificultad: Int) {
        when (dificultad) {
            1 -> {
                dDificultad.text = "Baja"
                dDificultad.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    android.R.color.holo_blue_light))
            }
            2 -> {
                dDificultad.text = "Media"
                dDificultad.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        android.R.color.holo_orange_light))
            }
            3 -> {
                dDificultad.text = "Alta"
                dDificultad.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.expert))
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?,
    ) {
        if(requestCode == Activity.RESULT_OK){
            actualizaVistas(DataHolder.currentUbication!!)
        }

        if (requestCode == RESULTADO_EDITAR) {
            if (adaptador != null) {
                ubicacion = adaptador.getItem(pos)
                _id = adaptador.getKey(pos)
                actualizaVistas(ubicacion!!)
            }
        } else if (requestCode == RESULTADO_GALERIA) {
            if (resultCode == Activity.RESULT_OK) {
                foto?.let { casoUso?.ponerFoto(pos, data!!.dataString, it) }
            } else {
                Toast.makeText(activity, "Foto no cargada", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == RESULTADO_FOTO) {
            if (resultCode == Activity.RESULT_OK && uriUltimaFoto != null) {
                ubicacion?.foto = uriUltimaFoto.toString()
                foto?.let { casoUso?.ponerFoto(pos, ubicacion?.foto, it) }
            } else {
                Toast.makeText(activity, "Error en captura", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vista_ubicacion, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.accion_compartir -> {
                ubicacion?.let { casoUso?.compartir(it) }
                true
            }
            R.id.accion_llegar -> {
                ubicacion?.let { casoUso?.verMapa(it) }
                true
            }
            R.id.accion_editar -> {
                casoUso?.editar(pos, RESULTADO_EDITAR)
                true
            }
            R.id.accion_borrar -> {
                val id = SelectorFragment.adaptador!!.getKey(pos)
                casoUso?.borrar(id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        const val RESULTADO_EDITAR = 1
        const val RESULTADO_GALERIA = 2
        const val RESULTADO_FOTO = 3
    }
}
