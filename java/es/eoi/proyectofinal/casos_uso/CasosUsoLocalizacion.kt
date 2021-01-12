package es.eoi.proyectofinal.casos_uso

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.Aplicacion
import es.eoi.proyectofinal.adaptador.AdaptadorUbicaciones


class CasosUsoLocalizacion(private val actividad: FragmentActivity?, private val codigoPermiso: Int) :
    LocationListener {
    private val manejadorLoc: LocationManager
    private var mejorLoc: Location? = null
    var posicionActual: GeoPoint
    private val adaptador: AdaptadorUbicaciones

    // CASOS DE USO
    fun activar() {
        if (hayPermisoLocalizacion()) activarProveedores()
    }

    fun desactivar() {
        if (hayPermisoLocalizacion()) manejadorLoc.removeUpdates(this)
    }

    fun permisoConcedido() {
        ultimaLocalizazion()
        activarProveedores()
        adaptador.notifyDataSetChanged()
    }

    fun hayPermisoLocalizacion(): Boolean {
        return (actividad?.let {
            ActivityCompat.checkSelfPermission(
                it, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
                == PackageManager.PERMISSION_GRANTED)
    }

    //FUNCIONES AUXILIARES
    @SuppressLint("MissingPermission")
    fun ultimaLocalizazion() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(
                    manejadorLoc.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                    )
                )
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                actualizaMejorLocaliz(
                    manejadorLoc.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                    )
                )
            }
        } else {
            solicitarPermiso(
                Manifest.permission.ACCESS_FINE_LOCATION,
                "Sin el permiso localización no puedo mostrar la distancia" +
                        " a las ubicaciones.", codigoPermiso, actividad
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun activarProveedores() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manejadorLoc.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    20 * 1000.toLong(), 5f, this
                )
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manejadorLoc.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    10 * 1000.toLong(),
                    10f,
                    this
                )
            }
        } else {
            solicitarPermiso(
                Manifest.permission.ACCESS_FINE_LOCATION,
                "Sin el permiso localización no puedo mostrar la distancia" +
                        " a las ubicaciones.", codigoPermiso, actividad
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d(TAG, "Nueva localización: $location")
        actualizaMejorLocaliz(location)
        adaptador.notifyDataSetChanged()
    }

    override fun onProviderDisabled(proveedor: String) {
        Log.d(TAG, "Se deshabilita: $proveedor")
        activarProveedores()
    }

    override fun onProviderEnabled(proveedor: String) {
        Log.d(TAG, "Se habilita: $proveedor")
        activarProveedores()
    }

    override fun onStatusChanged(proveedor: String, estado: Int, extras: Bundle) {
        Log.d(TAG, "Cambia estado: $proveedor")
        activarProveedores()
    }

    private fun actualizaMejorLocaliz(localiz: Location?) {
        if (localiz != null && (mejorLoc == null || localiz.accuracy < 2 * mejorLoc!!.accuracy || localiz.time - mejorLoc!!.time > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización")
            mejorLoc = localiz
            posicionActual = GeoPoint(localiz.latitude, localiz.longitude)
        }
    }

    companion object {
        private const val TAG = "Ubicaciones"
        fun solicitarPermiso(
            permiso: String,
            justificacion: String?,
            requestCode: Int,
            actividad: Activity?
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    actividad!!,
                    permiso
                )
            ) {
                AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton(
                        "Ok"
                    ) { dialog, whichButton ->
                        ActivityCompat.requestPermissions(
                            actividad,
                            arrayOf(permiso),
                            requestCode
                        )
                    }.show()
            } else {
                ActivityCompat.requestPermissions(actividad, arrayOf(permiso), requestCode)
            }
        }

        private const val DOS_MINUTOS = 2 * 60 * 1000.toLong()
    }

    init {
        manejadorLoc = actividad?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        posicionActual = (actividad.application as Aplicacion).posicionActual
        adaptador = (actividad.application as Aplicacion).adaptador!!
        ultimaLocalizazion()
    }
}

