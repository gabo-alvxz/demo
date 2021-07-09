package com.gabo.models.models.seller

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.gabo.models.R
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id : String,
    @SerializedName("nickname")
    val displayName : String,
    @SerializedName("permalink")
    val url : String,
    @SerializedName("seller_reputation")
    val reputation : Reputation
) : Parcelable {
    @Parcelize
    data class Reputation(
        @SerializedName("level_id")
        val level : ReputationLevel?,
        @SerializedName("power_seller_status")
        val status: ReputationStatus?
    ) : Parcelable

    enum class ReputationLevel(
        @ColorRes
        val color:  Int
    ){
        @SerializedName("1_red")
        RED(R.color.seller_reputation_red),
        @SerializedName("2_orange")
        ORANGE(R.color.seller_reputation_orange),
        @SerializedName("3_yellow")
        YELLOW(R.color.seller_reputation_yellow),
        @SerializedName("4_light_green")
        LIGHT_GREEN(R.color.seller_reputation_light_green),
        @SerializedName("5_green")
        GREEN(R.color.seller_reputation_green)
    }

    enum class ReputationStatus(
        @StringRes
        val stringRes : Int
    ) {
        @SerializedName("gold")
        GOLD(R.string.seller_reputation_gold),
        @SerializedName("platinum")
        PLATINUM(R.string.seller_reputation_platinum),
        @SerializedName("silver")
        SILVER(R.string.seller_reputation_silver)
    }
}
