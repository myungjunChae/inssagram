package com.ono.inssagram.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("image") @Expose val image: String,
    @SerializedName("username") @Expose val name: String,
    @SerializedName("post") @Expose val postCount: String,
    @SerializedName("following") @Expose val followingCount: String,
    @SerializedName("follower") @Expose val followerCount: String
)