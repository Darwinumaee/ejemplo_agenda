package com.umaee.agenda_contactos

data class Contacto (
    var id: Long = 0L,
    var nombre: String="",
    var telefono: String="",
    var email: String=""
)