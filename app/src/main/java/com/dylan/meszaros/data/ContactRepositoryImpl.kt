package com.dylan.meszaros.data

var contacts = mutableListOf(
    Contact(1, "Dylan Meszaros", "226-500-7709", "dylan@meszaros.com", "Family"),
    Contact(2, "Brayden Stone", "226-487-9955", "brayden@stone.com", "Work"),
)

class ContactRepositoryImpl: ContactRepository {
    override fun addContact(newContact: Contact): Contact {
        contacts.add(newContact);
        return newContact;
    }
    override fun editContact(contact: Contact): Contact {
        contacts[contact.id] = contact;
        return contact;
    }
    override fun getContacts(): List<Contact> {
        return contacts;
    }
}