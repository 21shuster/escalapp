package es.eoi.proyectofinal.modelo

import es.eoi.proyectofinal.R

enum class TipoUbicacion(val texto: String, val recurso: Int) {
    OTROS("Otros", R.drawable.otros),
    PAISAJE("Paisaje", R.drawable.paisaje),
    PARQUE("Parque", R.drawable.parque),
    CAMPING("Camping", R.drawable.camping),
    CICLISMO("Ciclismo",R.drawable.ciclismo),
    NATACION("Natacion", R.drawable.natacion),
    ESCALADA("Escalada",R.drawable.escalada),
    SENDERISMO("Senderismo", R.drawable.senderismo),
    PESCA("Pesca", R.drawable.pesca),
    NATURALEZA("Naturaleza", R.drawable.naturaleza),
    GASOLINERA("Gasolinera", R.drawable.gasolinera),
    ALOJAMIENTO("Alojamiento", R.drawable.hotel);

    companion object {
        val nombres: Array<String?>
            get() {
                val resultado = arrayOfNulls<String>(TipoUbicacion.values().size)
                for (tipo in TipoUbicacion.values()) {
                    resultado[tipo.ordinal] = tipo.texto
                }
                return resultado
            }

        fun get(nombre: String): List<TipoUbicacion> {
            return TipoUbicacion.values().filter { it.texto == nombre}
        }
    }
}
