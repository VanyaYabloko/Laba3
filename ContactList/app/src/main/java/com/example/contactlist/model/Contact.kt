package com.example.contactlist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val photoPath: String = ""

) : Parcelable