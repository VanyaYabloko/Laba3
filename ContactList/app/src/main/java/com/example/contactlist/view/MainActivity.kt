package com.example.contactlist.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactlist.adapter.ContactAdapter
import com.example.contactlist.databinding.ActivityMainBinding
import com.example.contactlist.model.Contact
import android.view.View
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactAdapter
    private val contacts = mutableListOf<Contact>()
    private var nextId = 1
    private val addContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(EXTRA_CONTACT, Contact::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra(EXTRA_CONTACT)
            }
            contact?.let {
                contacts.add(it.copy(id = nextId++))
                contactAdapter.notifyDataSetChanged()
                updateEmptyState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(contacts) { contact ->
            deleteContact(contact)
        }
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contactsRecyclerView.adapter = contactAdapter
    }

    private fun setupListeners() {
        binding.addContactButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            addContactLauncher.launch(intent)
        }
    }

    private fun deleteContact(contact: Contact) {
        contacts.remove(contact)
        contactAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (contacts.isEmpty()) {
            binding.emptyListText.visibility = View.VISIBLE
            binding.contactsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyListText.visibility = View.GONE
            binding.contactsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(EXTRA_CONTACT, Contact::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra(EXTRA_CONTACT)
            }
            contact?.let {
                contacts.add(it.copy(id = nextId++))
                contactAdapter.notifyDataSetChanged()
                updateEmptyState()
            }
        }
    }

    companion object {
        const val ADD_CONTACT_REQUEST = 1001
        const val EXTRA_CONTACT = "extra_contact"
    }
}