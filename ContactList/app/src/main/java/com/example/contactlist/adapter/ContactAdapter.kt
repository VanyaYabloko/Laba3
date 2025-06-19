package com.example.contactlist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contactlist.R
import com.example.contactlist.model.Contact
import java.io.File

class ContactAdapter(
    private var contacts: MutableList<Contact>,
    private val onDeleteClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contactName)
        val emailTextView: TextView = itemView.findViewById(R.id.contactEmail)
        val phoneTextView: TextView = itemView.findViewById(R.id.contactPhone)
        val photoImageView: ImageView = itemView.findViewById(R.id.contactPhoto)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.nameTextView.text = contact.name
        holder.emailTextView.text = contact.email
        holder.phoneTextView.text = contact.phone

        // Загрузка фото
        if (contact.photoPath.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(File(contact.photoPath))
                .placeholder(R.drawable.ic_default_contact)
                .into(holder.photoImageView)
        } else {
            holder.photoImageView.setImageResource(R.drawable.ic_default_contact)
        }

        holder.deleteButton.setOnClickListener { onDeleteClick(contact) }
    }
    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }
}