package es.eoi.proyectofinal.casos_uso

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.Aplicacion
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.EdicionUbicacionActivity
import es.eoi.proyectofinal.actividad.VistaUbicacionActivity
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesFirebase
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.modelo.Ubicacion
import es.eoi.proyectofinal.presentacion.SelectorFragment
import es.eoi.proyectofinal.presentacion.VistaUbicacionFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL


open class CasosUsoUbicacion(
    private val actividad: FragmentActivity?,
    private val fragment: Fragment?,
    private val ubicaciones: UbicacionesAsinc,
    private val adaptador: AdaptadorUbicacionesFirebase?
) {

    // OPERACIONES BÁSICAS
    fun actualizaPosLugar(pos: Int, lugar: Ubicacion?) {
        val id: String? = SelectorFragment.adaptador?.getKey(pos)
        guardar(id, lugar)
    }

    fun guardar(id: String?, nuevoLugar: Ubicacion?) {
        ubicaciones?.actualiza(id, nuevoLugar)
        SelectorFragment.adaptador?.notifyDataSetChanged()
    }

    fun buscar(cadena: String) {
        var lugar = null
        ubicaciones.filter(cadena, lugar)
        SelectorFragment.adaptador?.notifyDataSetChanged()
    }

    fun mostrar(pos: Int) {
        val fragmentVista: VistaUbicacionFragment? = obtenerFragmentVista()
        val ubi = SelectorFragment.adaptador!!.getItem(pos)
        ubi.id = SelectorFragment.adaptador?.getKey(pos)
        DataHolder.currentUbication = ubi
        if (fragmentVista != null) {
            fragmentVista.pos = pos
            fragmentVista._id = ubi.id
            fragmentVista.actualizaVistas(ubi)
        } else {
            val i = Intent(actividad, VistaUbicacionActivity::class.java)
            i.putExtra("pos", pos)
            actividad!!.startActivity(i)
        }
    }

    fun obtenerFragmentVista(): VistaUbicacionFragment? {
        val manejador = actividad!!.supportFragmentManager
        return manejador.findFragmentById(R.id.vista_ubicacion_fragment) as VistaUbicacionFragment?
    }

    fun editar(pos: Int, codidoSolicitud: Int) {
        val i = Intent(actividad, EdicionUbicacionActivity::class.java)
        i.putExtra("pos", pos)
        if (fragment != null) fragment!!.startActivityForResult(
            i,
            codidoSolicitud
        ) else actividad!!.startActivityForResult(i, codidoSolicitud)
    }

    fun borrar(id: String?) {
        ubicaciones?.borrar(id)
        SelectorFragment.adaptador?.notifyDataSetChanged()
        val manejador = actividad!!.supportFragmentManager
        if (manejador.findFragmentById(R.id.main_container) == null) {
            actividad!!.finish()
        } else {
            mostrar(0)
        }
    }

    fun nuevo() {
        val id: String? = ubicaciones?.nuevo()
        val size: Int = SelectorFragment.adaptador!!.getItemCount()
        val posicion: GeoPoint = (actividad!!.application as Aplicacion).posicionActual
        /*if (!posicion.equals(GeoPoint(0.0, 0.0))) {
            val lugar: Ubicacion = SelectorFragment.adaptador!!.getItem(size)
            lugar.posicion = posicion
            ubicaciones?.actualiza(id, lugar)
        }*/
        val i = Intent(actividad, EdicionUbicacionActivity::class.java)
        i.putExtra("_id", id)
        actividad!!.startActivity(i)
    }

    /**
     * Metodo que crea una nueva ubicacion a partir de una marca en el mapa
     * @param posicion
     */
    fun nuevo(posicion: GeoPoint) {
        DataHolder.currentUbication = null
        val i = Intent(actividad, EdicionUbicacionActivity::class.java)
        i.putExtra("lon", posicion.longitude)
        i.putExtra("lat", posicion.latitude)
        actividad!!.startActivity(i)
    }


    fun compartir(ubi: Ubicacion) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(
            Intent.EXTRA_TEXT,
            ubi.nombre.toString() + " - " + ubi.url
        )
        actividad!!.startActivity(i)
    }

    fun llamarTelefono(lugar: Ubicacion) {
        actividad!!.startActivity(
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.telefono)
            )
        )
    }

    fun verPgWeb(ubi: Ubicacion) {
        actividad!!.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(ubi.url)
            )
        )
    }

    fun verMapa(ubi: Ubicacion) {
        val lat: Double = ubi.posicion.latitude
        val lon: Double = ubi.posicion.longitude
        val uri = if (ubi.posicion !== GeoPoint(0.0, 0.0)) Uri.parse(
            "geo:$lat,$lon"
        ) else Uri.parse("geo:0,0?q=" + ubi.direccion)
        actividad!!.startActivity(Intent("android.intent.action.VIEW", uri))
    }


    // FOTOGRAFÍAS
    fun ponerDeGaleria(codidoSolicitud: Int) {
        val action: String
        action = if (Build.VERSION.SDK_INT >= 19) { // API 19 - Kitkat
            Intent.ACTION_OPEN_DOCUMENT
        } else {
            Intent.ACTION_PICK
        }
        val i = Intent(
            action,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        if (fragment != null) fragment!!.startActivityForResult(
            i,
            codidoSolicitud
        ) else actividad!!.startActivityForResult(i, codidoSolicitud)
    }

    fun ponerFoto(pos: Int, uri: String?, imageView: ImageView) {
        val ubicacion: Ubicacion =
            SelectorFragment.adaptador!!.getItem(pos) //lugares.elemento(pos);
        ubicacion.foto = uri
        visualizarFoto(ubicacion, imageView)
        actualizaPosLugar(pos, ubicacion)
    }

    fun visualizarFoto(ubi: Ubicacion, imageView: ImageView) {
        if (ubi.foto == null) {
            imageView.setImageBitmap(reduceBitmap(actividad, ubi.foto, 1024, 1024))
        } else {
            imageView.setImageBitmap(null)
        }
    }

    fun tomarFoto(codidoSolicitud: Int): Uri? {
        return try {
            val uriUltimaFoto: Uri
            val file = File.createTempFile(
                "img_" + System.currentTimeMillis() / 1000, ".jpg",
                actividad!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            uriUltimaFoto = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    actividad!!, "com.example.escalapp.fileprovider", file
                )
            } else {
                Uri.fromFile(file)
            }
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            i.putExtra(MediaStore.EXTRA_OUTPUT, uriUltimaFoto)
            if (fragment != null) fragment!!.startActivityForResult(
                i,
                codidoSolicitud
            ) else actividad!!.startActivityForResult(i, codidoSolicitud)
            uriUltimaFoto
        } catch (ex: IOException) {
            Toast.makeText(
                actividad, "Error al crear fichero de imagen",
                Toast.LENGTH_LONG
            ).show()
            null
        }
    }

    fun reduceBitmap(
        contexto: Context?, uri: String?,
        maxAncho: Int, maxAlto: Int
    ): Bitmap? {
        return try {
            var input: InputStream? = null
            val u = Uri.parse(uri)
            input = if (u.scheme == "http" || u.scheme == "https") {
                URL(uri).openStream()
            } else {
                contexto!!.contentResolver.openInputStream(u)
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inSampleSize = Math.max(
                Math.ceil(options.outWidth / maxAncho.toDouble()),
                Math.ceil(options.outHeight / maxAlto.toDouble())
            ).toInt()
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(input, null, options)
        } catch (e: FileNotFoundException) {
            Toast.makeText(
                contexto, "Fichero/recurso de imagen no encontrado",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
            null
        } catch (e: IOException) {
            Toast.makeText(
                contexto, "Error accediendo a imagen",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
            null
        }
    }
}
