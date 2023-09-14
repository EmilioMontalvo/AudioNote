package com.example.audionote

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.audionote.modelos.NotaDeVoz

class FRecyclerViewAdaptadorNotaDeVoz(
    private val contexto: PrincipalActivity,
    private val lista: ArrayList<NotaDeVoz>,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<FRecyclerViewAdaptadorNotaDeVoz.MyViewHolder>() {


    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val tituloTextView: TextView=view.findViewById(R.id.tv_titulo_rv)
        val descripcionTextView: TextView =view.findViewById(R.id.tv_descripcion)
        val fechaTextView:TextView =view.findViewById(R.id.tv_fecha)
        val buttonMore= view.findViewById<ImageButton>(R.id.btn_more)
        val buttonPlay=view.findViewById<ImageButton>(R.id.btn_play)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_vista_nota_voz,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    // Setear datos
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val notaActual=this.lista[position]
        holder.tituloTextView.text=notaActual.titulo
        holder.descripcionTextView.text=notaActual.descripcion
        holder.fechaTextView.text=notaActual.getFechaActualizacionString()
        holder.buttonMore.setOnClickListener{ view ->
            showPopupMenu(view,notaActual)
        }
    }

    override fun getItemCount(): Int {
        return this.lista.size
    }

    fun showPopupMenu(view: View,nota:NotaDeVoz) {
        val popupMenu = PopupMenu(contexto, view)
        popupMenu.inflate(R.menu.menu) // Crea un archivo XML de menú en res/menu/ con las opciones "Eliminar" y "Editar"
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.mi_ver -> {
                    contexto.irActividad(FormularioGrabacionActivity::class.java)
                    true
                }
                R.id.mi_eliminar -> {
                    contexto.eliminar(nota)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }





}