package com.forthgreen.app.repository.models

data class Contact(val id: String, var name:String, var isSection: Boolean = false, var buttonText: String = "Invite") {
    var numbers = ArrayList<String>()
    var emails = ArrayList<String>()
}