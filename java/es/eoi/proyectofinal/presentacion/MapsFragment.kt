package es.eoi.proyectofinal.presentacion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.VistaUbicacionActivity
import es.eoi.proyectofinal.adaptador.AdaptadorUbicacionesMap
import es.eoi.proyectofinal.casos_uso.CasosUsoLocalizacion
import es.eoi.proyectofinal.casos_uso.CasosUsoUbicacion
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase
import es.eoi.proyectofinal.modelo.TipoUbicacionMap
import es.eoi.proyectofinal.modelo.Ubicacion

class MapsFragment() : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerDragListener{

    var mapa: GoogleMap? = null
    var ubicaciones: UbicacionesAsinc? = null
    var usoLocalizacion: CasosUsoLocalizacion? = null
    var casoUso: CasosUsoUbicacion? = null
    var marker: Marker? = null
    lateinit var db: FirebaseFirestore
    var ubiList = mutableMapOf<String, Ubicacion>()
    var listaUbicaciones = ArrayList<Ubicacion>()
    var ubicacion: Ubicacion? = null
    val TAG = "myApp"

    private var _id = -1
    var adaptador: AdaptadorUbicacionesMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        activity?.actionBar?.title = "Map"
        db = Firebase.firestore
        ubicaciones = UbicacionesFirebase()
        usoLocalizacion = CasosUsoLocalizacion(activity, 1)
        listaUbicaciones = getUbicaciones()
        casoUso = CasosUsoUbicacion(activity, this, ubicaciones as UbicacionesFirebase, SelectorFragment.adaptador)
        //val extras: Bundle? = getIntent().getExtras()
        //if (extras != null) _id = extras.getInt("_id", -1)
    }

    fun getUbicaciones() : ArrayList<Ubicacion> {
        var lista = ArrayList<Ubicacion>()
        db.collection("ubicaciones").get().addOnSuccessListener { result ->
            for (document in result) {
                var ubi = document.toObject(Ubicacion::class.java)
                Log.d(TAG, "${document.id} => ${ubi.nombre}")
                ubiList[document.id] = ubi
                ubi.id = document.id
                addMapa(ubi)
                lista.add(ubi)
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
        }
        return lista
    }

    /**
     * Este método se va a llamar cuando el mapa este listo para usarse y Obtengamos un objeto GoogleMap no nulo
     *
     * @param googleMap
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        mapa = googleMap
        mapa?.mapType = GoogleMap.MAP_TYPE_NORMAL
        mapa?.setOnMapClickListener(this)
        mapa?.setOnMarkerDragListener(this)
        if (usoLocalizacion!!.hayPermisoLocalizacion()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            //mapa!!.setMyLocationEnabled(true)
            mapa!!.isMyLocationEnabled = true
        }
        mapa?.uiSettings?.isZoomControlsEnabled = true
        mapa?.uiSettings?.isCompassEnabled = true
        mapa?.mapType = 3
        if (_id >= 0) {
            val lugar: Ubicacion = listaUbicaciones[_id]
            val p: GeoPoint = lugar.posicion
            mapa?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(p.latitude, p.longitude), 12f
                )
            )
            val iGrande = BitmapFactory.decodeResource(
                resources, lugar.tipoEnum!!.recurso
            )
            val icono = Bitmap.createScaledBitmap(
                iGrande,
                iGrande.width / 7, iGrande.height / 7, false
            )
            marker = mapa?.addMarker(
                MarkerOptions()
                    .position(LatLng(p.latitude, p.longitude))
                    .draggable(true)
                    .title(lugar.nombre).snippet(lugar.direccion)
                    .icon(BitmapDescriptorFactory.fromBitmap(icono))
            )
            mapa?.setOnInfoWindowClickListener(this)
            mapa?.setOnMarkerDragListener(this)
        } else {
            if (listaUbicaciones.size > 0) {
                val p: GeoPoint = listaUbicaciones[0].posicion
                mapa?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(p.latitude, p.longitude), 12f
                    )
                )
            }
            for (n in 0 until listaUbicaciones.size) {
                val lugar: Ubicacion = listaUbicaciones[n]
                val p: GeoPoint = lugar.posicion
                val iGrande = BitmapFactory.decodeResource(
                    resources, lugar.tipoEnum!!.recurso
                )
                val icono = Bitmap.createScaledBitmap(
                    iGrande,
                    iGrande.width / 7, iGrande.height / 7, false
                )
                mapa?.addMarker(
                    MarkerOptions()
                        .position(LatLng(p.latitude, p.longitude))
                        .title(lugar.nombre).snippet(lugar.direccion)
                        .icon(BitmapDescriptorFactory.fromBitmap(icono))
                )
            }
            mapa?.setOnInfoWindowClickListener(this)
        }
    }

    fun addMapa(ubicacion: Ubicacion){
        val p: GeoPoint = ubicacion.posicion
        mapa?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(p.latitude, p.longitude), 8f
            )
        )
        val iGrande = BitmapFactory.decodeResource(
            resources, TipoUbicacionMap.get(ubicacion.tipoEnum!!.ordinal)[0].recurso
        )
        val icono = Bitmap.createScaledBitmap(
            iGrande,
            iGrande.width / 2, iGrande.height / 2, false
        )
        mapa?.addMarker(
            MarkerOptions()
                .position(LatLng(p.latitude, p.longitude))
                .title(ubicacion.nombre).snippet(ubicacion.direccion)
                .icon(BitmapDescriptorFactory.fromBitmap(icono))
        )

        mapa?.setOnInfoWindowClickListener(this)
    }

    override fun onStart(){
        super.onStart()
        adaptador?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        adaptador?.stopListening()
    }

    /**
     * Evento que lanza una actividad [VistaUbicacionActivity] cuando se pulsa algun lugar marcado en el mapa
     *
     * @param marker
     */
    override fun onInfoWindowClick(marker: Marker) {
        if (marker.title == "Mi nueva ubicación") {
            casoUso?.nuevo(GeoPoint(marker.position.longitude, marker.position.latitude))
            DataHolder.currentUbication?.let { addMapa(it) }
            //finish()
        } else {
            ubiList.forEach { (key, value) ->
                if(value.nombre.equals(marker.title)){
                    DataHolder.currentUbication = value
                    val intent = Intent(activity, VistaUbicacionActivity::class.java)
                    intent.putExtra("id", key)
                    intent.putExtra("pos", id)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onMapClick(puntoPulsado: LatLng?) {
        puntoPulsado?.let {
            if (marker == null) {
                marker = mapa?.addMarker(
                    MarkerOptions().position(it)
                        .title("Mi nueva ubicación")
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                        )
                        .draggable(true)
                )
            } else marker!!.position = puntoPulsado
        }
    }


    override fun onMarkerDragStart(marker: Marker?) {}

    override fun onMarkerDrag(marker: Marker?) {}

    override fun onMarkerDragEnd(marker: Marker) {
        for (id in 0 until listaUbicaciones.size) {
            if (listaUbicaciones[id].nombre.equals(marker.title)
            ) {
                ubicacion = listaUbicaciones[id]
                ubicacion!!.posicion = GeoPoint(marker.position.longitude, marker.position.latitude)
                casoUso?.guardar(SelectorFragment.adaptador!!.getKey(id), ubicacion)
            }
        }
    }
}