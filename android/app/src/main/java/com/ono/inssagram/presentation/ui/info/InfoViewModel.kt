package com.ono.inssagram.presentation.ui.info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ono.inssagram.datasource.UserApi
import com.ono.inssagram.model.UserInfo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class InfoViewModel(private val api: UserApi) : ViewModel() {

    val fragmentList = listOf(InfoFirstFragment(), InfoSecondFragment(), InfoThirdFragment())
    val currentPage = MutableLiveData<Int>()
    val userInfo = MutableLiveData<UserInfo>()

    private val compositeDisposable = CompositeDisposable()

    init {
        currentPage.postValue(0)
    }

    fun getUserInfo(id: String) {
        compositeDisposable.add(
            api.getUserInfo(id)
                .subscribeOn(Schedulers.io())
                .subscribe({userInfo.postValue(it)}, { e -> println(e) })
        )
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}