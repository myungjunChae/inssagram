package com.ono.inssagram.presentation.ui.main

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ono.inssagram.R
import com.ono.inssagram.datasource.PredictApi
import com.ono.inssagram.model.PredictItem
import com.ono.inssagram.model.PredictedImage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class MainViewModel(private val api : PredictApi) : ViewModel(){

    val URL =
        "https://scontent-gmp1-1.cdninstagram.com/vp/6bf8fae3749af945e192c91675d1f705/5E57D7A8/t51.2885-15/e35/s1080x1080/75300797_1448942718605512_5262792356104992691_n.jpg?_nc_ht=scontent-gmp1-1.cdninstagram.com&amp;_nc_cat=104"

    val predictItemList = listOf(
        PredictItem(R.drawable.predict_image_1, "팔로워 수와 사진 기반 예측"),
        PredictItem(R.drawable.predict_image_1, "재방문 팔로워 등을 통한 고도화된 예측")
    )

    val livePredictedImages = MutableLiveData<List<PredictedImage>>()
    val predictedImages : MutableList<PredictedImage> = arrayListOf()

    private val compositeDisposable = CompositeDisposable()

    fun addPredictedImages(item : PredictedImage){
        predictedImages.add(item)
        livePredictedImages.postValue(predictedImages)
    }

    fun getLike(follower : String, bitmap: Bitmap){
        compositeDisposable.add(
            api.getLike(follower, convert(bitmap))
                .subscribeOn(Schedulers.io())
                .map { addPredictedImages(PredictedImage(it, bitmap)) }
                .subscribe({},{e -> println(e)})
        )
    }

    fun convert(bitmap : Bitmap) : String{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}