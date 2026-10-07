package com.umaee.agenda_contactos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: ContactosDBHelper
    private lateinit var adapter: ContactoAdapter
    private lateinit var etNombre: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBuscar: EditText
    private lateinit var lvContactos: ListView
    private lateinit var btnGuardar: Button
    private lateinit var btnLimpiar: Button
    /** 0 = modo nuevo; > 0 = estamos editando ese contacto. */
    private var idSeleccionado: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enlazarVistas()

        dbHelper = ContactosDBHelper(this)
        adapter = ContactoAdapter(this)
        lvContactos.adapter = adapter

        btnGuardar.setOnClickListener { guardarContacto() }
        btnLimpiar.setOnClickListener { limpiarFormulario() }

        // Click corto → cargar en formulario (modo edición)
        lvContactos.setOnItemClickListener { _, _, position, _ ->
            cargarEnFormulario(adapter.getItem(position))
        }

        // Click largo → confirmar eliminación
        lvContactos.setOnItemLongClickListener { _, _, position, _ ->
            confirmarEliminacion(adapter.getItem(position))
            true  // Consumimos el evento
        }

        // Búsqueda en vivo
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                cargarContactos()
            }
        })

        cargarContactos()
    }

    // ---------------------------------------------------------------
    // Utilidades de UI
    // ---------------------------------------------------------------

    private fun enlazarVistas() {
        etNombre = findViewById(R.id.etNombre)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        etBuscar = findViewById(R.id.etBuscar)
        lvContactos = findViewById(R.id.lvContactos)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
    }

    private fun cargarContactos() {
        val texto = etBuscar.text.toString().trim()
        val lista = if (texto.isEmpty()) dbHelper.obtenerTodos() else dbHelper.buscar(texto)
        adapter.actualizarLista(lista)
    }

    private fun cargarEnFormulario(contacto: Contacto) {
        idSeleccionado = contacto.id
        etNombre.setText(contacto.nombre)
        etTelefono.setText(contacto.telefono)
        etEmail.setText(contacto.email)
        btnGuardar.text = "Actualizar"
        etNombre.requestFocus()
    }

    private fun limpiarFormulario() {
        idSeleccionado = 0L
        etNombre.setText("")
        etTelefono.setText("")
        etEmail.setText("")
        etNombre.error = null
        btnGuardar.text = "Guardar"
    }

    // ---------------------------------------------------------------
    // Operaciones CRUD
    // ---------------------------------------------------------------

    private fun guardarContacto() {
        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val email = etEmail.text.toString().trim()

        // Validación mínima
        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return
        }

        val contacto = Contacto(idSeleccionado, nombre, telefono, email)

        val mensaje = if (idSeleccionado == 0L) {
            dbHelper.insertar(contacto)
            "Contacto agregado"
        } else {
            dbHelper.actualizar(contacto)
            "Contacto actualizado"
        }

        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        limpiarFormulario()
        cargarContactos()
    }

    private fun confirmarEliminacion(contacto: Contacto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar contacto")
            .setMessage("¿Deseas eliminar a \"${contacto.nombre}\"?")
            .setPositiveButton("Sí") { _, _ ->
                dbHelper.eliminar(contacto.id)
                if (idSeleccionado == contacto.id) limpiarFormulario()
                cargarContactos()
                Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}