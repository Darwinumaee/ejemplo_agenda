package com.umaee.agenda_contactos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ContactoAdapter (
    private val context: Context,
    private val listaContactos: MutableList<Contacto> = mutableListOf()
): BaseAdapter(){
    override fun getCount(): Int = listaContactos.size
    override fun getItem(position: Int): Contacto = listaContactos[position]
    override fun getItemId(position: Int): Long = listaContactos[position].id

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val vista = convertView ?: LayoutInflater.from(context).
        inflate(R.layout.item_contacto, parent, false)
        val contacto = listaContactos[position]
        vista.findViewById<TextView>(R.id.tvNombre).text = contacto.nombre
        vista.findViewById<TextView>(R.id.tvTelefono).text = contacto.telefono
        vista.findViewById<TextView>(R.id.tvEmail).text = contacto.email
        return vista
    }

    fun actualizarLista(nuevaLista: List<Contacto>){
     listaContactos.clear()
        listaContactos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}