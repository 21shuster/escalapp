package es.eoi.proyectofinal

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.firestore.GeoPoint
import es.eoi.proyectofinal.adaptador.AdaptadorUbicaciones
import es.eoi.proyectofinal.datos.UbicacionesAsinc
import es.eoi.proyectofinal.datos.UbicacionesFirebase


class Aplicacion : Application() {
    var ubicaciones: UbicacionesAsinc? = null
    var adaptador: AdaptadorUbicaciones? = null
    var posicionActual: GeoPoint = GeoPoint(0.0, 0.0)
    override fun onCreate() {
        super.onCreate()
        ubicaciones = UbicacionesFirebase()
        adaptador = AdaptadorUbicaciones(this.baseContext, ubicaciones as UbicacionesFirebase)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
