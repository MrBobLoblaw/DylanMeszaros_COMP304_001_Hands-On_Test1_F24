package com.dylan.meszaros.viewmodel

import androidx.lifecycle.ViewModel
import com.dylan.meszaros.data.Contact
import com.dylan.meszaros.data.ContactRepository

class ContactViewModel (
    private val contactsRepository: ContactRepository
): ViewModel() {

    fun getContacts() = contactsRepository.getContacts()
    fun addContact(newContact: Contact) = contactsRepository.addContact(newContact)
    fun editContact(contact: Contact) = contactsRepository.editContact(contact)
}