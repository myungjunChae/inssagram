package com.ono.inssagram

import com.ono.inssagram.datasource.PredictApi
import com.ono.inssagram.datasource.UserApi
import com.ono.inssagram.presentation.ui.info.InfoViewModel
import com.ono.inssagram.presentation.ui.main.MainViewModel
import com.ono.inssagram.presentation.ui.splash.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun injectionFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        listOf(
            viewModelModule,
            remoteModule
        )
    )
}

val viewModelModule: Module = module {
    viewModel { SplashViewModel() }
    viewModel { InfoViewModel(get()) }
    viewModel { MainViewModel(get()) }
}

val remoteModule: Module = module {
    single { userApi }
    single { predictApi }
}

private const val BASE_URL = "http://35.243.107.176:9597/"
//private const val BASE_URL = "http://172.16.209.32:9597/"

private val interceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.MINUTES)
    .writeTimeout(5, TimeUnit.MINUTES)
    .readTimeout(5, TimeUnit.MINUTES)
    .addInterceptor(interceptor).build()



private val retrofit: Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Call 객체가 아닌 RxJava 형태로 받기 위함
        .build()


private val userApi: UserApi = retrofit.create(UserApi::class.java)
private val predictApi: PredictApi = retrofit.create(PredictApi::class.java)

private const val AUTHORIZE = "AUTHORIZE"
private const val ACCOUNT = "ACCOUNT"
private const val CATEGORY = "CATEGORY"
private const val ROUTINE = "ROUTINE"