package es.eoi.proyectofinal.modelo

import es.eoi.proyectofinal.R

enum class TipoUbicacionMap(val texto: String, val recurso: Int) {
    OTROS("Otros", R.drawable.otros),
    PAISAJE("Paisaje", R.drawable.locationphoto),
    PARQUE("Parque", R.drawable.locationpark),
    CAMPING("Camping", R.drawable.locationcamp),
    CICLISMO("Ciclismo",R.drawable.locationbike),
    NATACION("Natacion", R.drawable.locationswim),
    ESCALADA("Escalada",R.drawable.locationclimb),
    SENDERISMO("Senderismo", R.drawable.locationhike),
    PESCA("Pesca", R.drawable.locationfish),
    NATURALEZA("Naturaleza", R.drawable.locationspot),
    GASOLINERA("Gasolinera", R.drawable.locationpetrol),
    ALOJAMIENTO("Alojamiento", R.drawable.locationhost);

    companion object {
        val nombres: Array<String?>
            get() {
                val resultado = arrayOfNulls<String>(TipoUbicacionMap.values().size)
                for (tipo in TipoUbicacionMap.values()) {
                    resultado[tipo.ordinal] = tipo.texto
                }
                return resultado
            }

        fun get(pos: Int): List<TipoUbicacionMap> {
            return TipoUbicacionMap.values().filter { it.ordinal == pos}
        }
    }
}
