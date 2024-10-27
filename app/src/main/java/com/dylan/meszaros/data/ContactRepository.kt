package com.dylan.meszaros.data

interface ContactRepository {
    fun addContact(newContact: Contact): Contact
    fun editContact(contact: Contact): Contact
    fun getContacts(): List<Contact>
}