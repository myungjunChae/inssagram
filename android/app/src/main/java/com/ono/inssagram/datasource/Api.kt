package com.ono.inssagram.datasource

import com.ono.inssagram.model.UserInfo
import io.reactivex.Single
import retrofit2.http.*

interface UserApi{
    @GET("main/user")
    fun getUserInfo(@Query("id") id: String): Single<UserInfo>
}

interface PredictApi{
    @POST("main/predict")
    @FormUrlEncoded
    fun getLike(@Field("follower") follower : String, @Field("picture") picture : String): Single<Int>
}