package com.ono.inssagram.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.ono.inssagram.R
import com.ono.inssagram.databinding.ActivityMainBinding
import com.ono.inssagram.databinding.ViewPredictedImageBinding
import com.ono.inssagram.databinding.ViewPredictionItemBinding
import com.ono.inssagram.datasource.PREF_NAME
import com.ono.inssagram.datasource.PreferenceModel
import com.ono.inssagram.model.PredictItem
import com.ono.inssagram.model.PredictedImage
import com.ono.inssagram.presentation.ui.info.InfoViewModel
import com.softdough.grow.presentation.base.BaseActivity
import com.softdough.grow.util.ScreenUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.math.abs

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val vm: MainViewModel by viewModel()
    private val pref by lazy { MainPref(this, PREF_NAME) }

    private val PICK_FROM_ALBUM = 1

    override val resourceId: Int = R.layout.activity_main
    override val statusBarColor: Int = R.color.colorWhite



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = statusBarColor

        Glide.with(this)
            .load(pref.image)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.userImage)

        binding.userName.text = pref.name
        binding.postCount.text = pref.postCount
        binding.followingCount.text = pref.followingCount
        binding.followerCount.text = pref.followerCount

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

            adapter = PredictRecyclerAdapter().apply {
                onPropagationCallback = { selectImageInAlbum() }

                vm.predictItemList.forEach {
                    addItem(it)
                }
                notifyDataSetChanged()
            }
        }

        vm.livePredictedImages.observe(this, Observer { renderAfterPredict() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK && null != data) {
                val bitmap = decodeUri(this, data.data!!, 150)
                binding.loadingPanel.visibility = View.VISIBLE
                binding.masking.visibility = View.VISIBLE
                vm.getLike(pref.followerCount!!, bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }

    }
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    private fun renderAfterPredict() {
        Toast.makeText(this, "예측이 완료되었습니다.", Toast.LENGTH_SHORT).show()

        binding.predictResultText.text = "예측 결과"
        binding.loadingPanel.visibility = View.INVISIBLE
        binding.masking.visibility = View.INVISIBLE
        binding.notFindImageView.visibility = View.INVISIBLE
        binding.predictGridLayout.visibility = View.VISIBLE

        binding.predictGridLayout.apply {
            removeAllViews()

            var childWidth = (ScreenUtil.deviceWidth() - (ScreenUtil.DP_16 * 3)) / 2

            vm.livePredictedImages.value?.forEachIndexed { index, item ->
                var viewRoutinebinding: ViewPredictedImageBinding=
                    DataBindingUtil.inflate(layoutInflater, R.layout.view_predicted_image, this, false)

                var params = ConstraintLayout.LayoutParams(viewRoutinebinding.root.layoutParams)

                viewRoutinebinding.root.layoutParams = params.apply {
                    width = childWidth
                    leftMargin = ScreenUtil.DP_16

                    if (index >= 2) {
                        topMargin = ScreenUtil.DP_14 * 2
                    }
                }

                viewRoutinebinding.imageView.setImageBitmap(item.image)
                viewRoutinebinding.textView.text = item.likeCount.toString()

                this.addView(viewRoutinebinding.root)
            }
        }
    }

    private fun decodeUri(c : Context, uri : Uri, requiredSize : Int) : Bitmap{
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o);

        var widthTemp = o.outWidth
        var heightTemp = o.outHeight
        var scale = 1

        while(true) {
            if(widthTemp / 2 < requiredSize || heightTemp / 2 < requiredSize)
                break
            widthTemp /= 2
            heightTemp /= 2
            scale *= 2
        }

        val o2 = BitmapFactory.Options();
        o2.inSampleSize = scale

        return BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)!!;
    }

}

class PredictRecyclerAdapter : RecyclerView.Adapter<PredictRecyclerAdapter.ItemViewHolder>() {
    private lateinit var context: Context
    private val itemList: MutableList<PredictItem> = mutableListOf()

    private lateinit var viewHolder: ItemViewHolder

    private val VIEW_TYPE_FIRST = 0
    private val VIEW_TYPE_LAST = 1

    var onPropagationCallback: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context

        var inflater = LayoutInflater.from(context)

        //동적 margin
        var firstParams = LinearLayoutCompat.LayoutParams(
            ScreenUtil.percentToPxWidth(0.76F).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            leftMargin = ScreenUtil.percentToPxWidth(0.045F).toInt()
        }

        var lastParams = LinearLayoutCompat.LayoutParams(
            ScreenUtil.percentToPxWidth(0.76F).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            leftMargin = ScreenUtil.percentToPxWidth(0.067F).toInt()
            rightMargin = ScreenUtil.percentToPxWidth(0.045F).toInt()
        }

        when (viewType) {
            VIEW_TYPE_FIRST -> {
                var binding = DataBindingUtil.inflate<ViewPredictionItemBinding>(
                    inflater,
                    R.layout.view_prediction_item,
                    parent,
                    false
                )
                binding.predictItemWrapper.apply {
                    layoutParams = firstParams
                }
                viewHolder = ItemViewHolder(binding, context).apply {
                    onPredictCallback = {
                        onPropagationCallback?.invoke()
                    }
                }
            }
            VIEW_TYPE_LAST -> {
                var binding = DataBindingUtil.inflate<ViewPredictionItemBinding>(
                    inflater,
                    R.layout.view_prediction_item,
                    parent,
                    false
                )
                binding.predictItemWrapper.apply {
                    layoutParams = lastParams
                }
                viewHolder = ItemViewHolder(binding, context)
            }
        }

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_FIRST
            else -> VIEW_TYPE_LAST
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    fun addItem(item: PredictItem) {
        itemList.add(item)
    }

    class ItemViewHolder(
        private var binding: ViewPredictionItemBinding,
        private var context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        var onPredictCallback: (() -> Unit)? = null

        init {
            this.itemView.setOnClickListener {
                onPredictCallback?.invoke()
            }
        }

        fun bindData(data: PredictItem) {
            binding.imageView.background = context.getDrawable(data.resourceId)
            binding.textView.text = data.text
        }
    }
}

private class MainPref(context: Context, name: String) : PreferenceModel(context, name) {
    var image by stringPreference("image", null)
    var name by stringPreference("name", null)
    var postCount by stringPreference("postCount", null)
    var followingCount by stringPreference("followingCount", null)
    var followerCount by stringPreference("followerCount", null)
}