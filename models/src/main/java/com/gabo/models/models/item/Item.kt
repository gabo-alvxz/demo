package com.gabo.models.models.item

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.StringRes
import com.gabo.models.R
import com.gabo.models.models.image.Picture
import com.gabo.models.models.seller.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

@Parcelize
data class Item(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("currency_id")
    val currencyId : String,

    @SerializedName("sold_quantity")
    val soldQuantity: Int,

    @SerializedName("thumbnail")
    val thumbnail : String?,

    @SerializedName("permalink")
    val url : String,

    @SerializedName("available_quantity")
    val availableQuantity: Int,

    @SerializedName("attributes")
    val attributes: List<Attribute>,

    @SerializedName("accepts_mercadopago")
    val acceptsMercadoPago: Boolean,

    @SerializedName("condition")
    val condition:  Condition?,

    @SerializedName("installments")
    val installments: Installments?,

    @SerializedName("pictures")
    val pictures : List<Picture>? = null,

    @SerializedName("seller_id")
    val sellerId : String?,

    @SerializedName("warranty")
    val warranty : String? = null


) : Parcelable {
    data class Description(
        @SerializedName("plain_text")
        val text: String
    )
    @Parcelize
    data class Attribute(
        @SerializedName("name")
        val name: String,
        @SerializedName("value_name")
        val valueName: String
    ) : Parcelable

    @Parcelize
    data class Installments(
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("currency_id")
        val currency: String
    ) : Parcelable

    enum class Condition {
        @SerializedName("new")
        NEW,
        @SerializedName("used")
        USED;
    }
}