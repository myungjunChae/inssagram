package com.ono.inssagram.presentation.ui.splash

import android.os.Bundle
import android.os.Handler
import com.ono.inssagram.R
import com.ono.inssagram.databinding.ActivitySplashBinding
import com.ono.inssagram.presentation.startActivityWithFinish
import com.ono.inssagram.presentation.ui.info.InfoActivity
import com.softdough.grow.presentation.base.BaseActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(){
    override val resourceId: Int = R.layout.activity_splash
    override val statusBarColor: Int = R.color.colorWhite
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding

        handler.postDelayed({
            startActivityWithFinish<InfoActivity>()
        }, 3000)
    }
}