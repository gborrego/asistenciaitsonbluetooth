package com.itson.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Justificante( public val id: Long? = null,
                         public val nota: String,
                         public val foto: String?,) : Parcelable
