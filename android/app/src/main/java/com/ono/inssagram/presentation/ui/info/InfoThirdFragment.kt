package com.ono.inssagram.presentation.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ono.inssagram.R
import com.ono.inssagram.databinding.FragmentInfoDefaultBinding
import com.ono.inssagram.databinding.FragmentInfoLastBinding
import com.softdough.grow.presentation.base.BaseFragment

class InfoThirdFragment : BaseFragment<FragmentInfoLastBinding>(){
    override val resourceId: Int = R.layout.fragment_info_last

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return binding.root
    }
}