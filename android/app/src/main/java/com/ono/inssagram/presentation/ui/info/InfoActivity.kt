package com.ono.inssagram.presentation.ui.info

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ono.inssagram.R
import com.ono.inssagram.databinding.ActivityInfoBinding
import com.ono.inssagram.databinding.ViewInfoPagerItemBinding
import com.ono.inssagram.datasource.PREF_NAME
import com.ono.inssagram.datasource.PreferenceModel
import com.ono.inssagram.injectionFeature
import com.ono.inssagram.presentation.startActivityWithFinish
import com.ono.inssagram.presentation.ui.main.MainActivity
import com.softdough.grow.presentation.base.BaseActivity
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import java.util.*

class InfoActivity : BaseActivity<ActivityInfoBinding>() {

    private val vm: InfoViewModel by viewModel()
    private val pref by lazy { InfoPref(this, PREF_NAME) }

    override val resourceId: Int = R.layout.activity_info
    override val statusBarColor: Int = R.color.colorWhite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(applicationContext)
            injectionFeature()
        }

        vm.currentPage.observe(this, androidx.lifecycle.Observer { changePage() })
        vm.userInfo.observe(this, androidx.lifecycle.Observer { saveUserInfo() })

        binding.viewPager.apply {
            adapter = InfoAdapter(vm.fragmentList, supportFragmentManager)

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    vm.currentPage.postValue(position)
                }
            })
        }

        binding.indicator.apply {
            pageCount = vm.fragmentList.size
            updateDotState()
        }

        binding.nextButton.setOnClickListener {
            when (vm.currentPage.value == 2) {
                true -> {
                    //TODO 서버 활성화
                    Log.i("", "testing")
                    binding.loadingPanel.visibility = View.VISIBLE
                    binding.masking.visibility = View.VISIBLE
                    vm.getUserInfo(binding.invisibleEditText.text.toString())
                }
                false -> {
                    Log.i("", "not")

                }
            }
        }
    }

    private fun changePage() {
        binding.indicator.apply {
            currentPage = vm.currentPage.value!!
            updateDotState()
        }

        when (vm.currentPage.value == 2) {
            true -> {
                binding.constraint.background = this.getDrawable(android.R.color.transparent)
                binding.invisibleTextView.visibility = View.VISIBLE
                binding.invisibleEditText.visibility = View.VISIBLE
                binding.nextButton.text = "시작하기"
            }
            false -> {
                binding.constraint.background = this.getDrawable(R.drawable.info_background)
                binding.invisibleTextView.visibility = View.INVISIBLE
                binding.invisibleEditText.visibility = View.INVISIBLE
                binding.nextButton.text = "다음으로"
            }
        }
    }

    private fun saveUserInfo() {
        pref.image = vm.userInfo.value?.image
        pref.name = vm.userInfo.value?.name
        pref.postCount = vm.userInfo.value?.postCount
        pref.followingCount = vm.userInfo.value?.followingCount
        pref.followerCount = vm.userInfo.value?.followerCount

        startActivityWithFinish<MainActivity>()
    }
}


class InfoAdapter(private val fragments: List<Fragment>, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}

private class InfoPref(context: Context, name: String) : PreferenceModel(context, name) {
    var image by stringPreference("image", null)
    var name by stringPreference("name", null)
    var postCount by stringPreference("postCount", null)
    var followingCount by stringPreference("followingCount", null)
    var followerCount by stringPreference("followerCount", null)
}