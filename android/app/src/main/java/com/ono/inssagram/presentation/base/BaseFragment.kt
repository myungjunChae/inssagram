package com.softdough.grow.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T_DATA_BINDING : ViewDataBinding> :
    Fragment() {

    abstract val resourceId: Int

    lateinit var binding: T_DATA_BINDING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<T_DATA_BINDING>(inflater, resourceId, container, false)

        return null
    }
}