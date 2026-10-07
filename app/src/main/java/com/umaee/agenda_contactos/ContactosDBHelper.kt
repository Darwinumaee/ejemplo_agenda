package com.umaee.agenda_contactos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactosDBHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "contactos.db"
        private const val VERSION_BD = 1

        const val TABLA = "contactos"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_TELEFONO = "telefono"
        const val COL_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """ 
            CREATE TABLE $TABLA ( 
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COL_NOMBRE TEXT NOT NULL, 
                $COL_TELEFONO TEXT, 
                $COL_EMAIL TEXT 
            ) 
        """.trimIndent()
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    // ---------- CREATE ----------
    fun insertar(contacto: Contacto): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(COL_NOMBRE, contacto.nombre)
            put(COL_TELEFONO, contacto.telefono)
            put(COL_EMAIL, contacto.email)
        }
        val id = db.insert(TABLA, null, valores)
        db.close()
        return id
    }

    // ---------- UPDATE ----------
    fun actualizar(contacto: Contacto): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(COL_NOMBRE, contacto.nombre)
            put(COL_TELEFONO, contacto.telefono)
            put(COL_EMAIL, contacto.email)
        }
        val filas = db.update(
            TABLA, valores,
            "$COL_ID = ?",
            arrayOf(contacto.id.toString())
        )
        db.close()
        return filas
    }

    // ---------- DELETE ----------
    fun eliminar(id: Long): Int {
        val db = writableDatabase
        val filas = db.delete(TABLA, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }

    // ---------- READ ----------
    fun obtenerTodos(): List<Contacto> {
        val lista = mutableListOf<Contacto>()
        val db = readableDatabase
        val cursor = db.query(TABLA, null, null, null, null, null, "$COL_NOMBRE ASC")
        cursor.use {
            while (it.moveToNext()) lista.add(cursorAContacto(it))
        }
        db.close()
        return lista
    }

    // ---------- SEARCH ----------
    fun buscar(texto: String): List<Contacto> {
        val lista = mutableListOf<Contacto>()
        val db = readableDatabase
        val seleccion = "$COL_NOMBRE LIKE ? OR $COL_TELEFONO LIKE ? OR $COL_EMAIL LIKE?"
        val args = arrayOf("%$texto%", "%$texto%", "%$texto%")
        val cursor = db.query(TABLA, null, seleccion, args, null, null, "$COL_NOMBRE ASC")
        cursor.use {
            while (it.moveToNext()) lista.add(cursorAContacto(it))
        }
        db.close()
        return lista
    }

    private fun cursorAContacto(c: Cursor): Contacto = Contacto(
        id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
        nombre = c.getString(c.getColumnIndexOrThrow(COL_NOMBRE)) ?: "",
        telefono = c.getString(c.getColumnIndexOrThrow(COL_TELEFONO)) ?: "",
        email = c.getString(c.getColumnIndexOrThrow(COL_EMAIL)) ?: ""
    )
}