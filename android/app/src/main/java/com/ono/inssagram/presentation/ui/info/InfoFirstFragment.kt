package com.ono.inssagram.presentation.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ono.inssagram.R
import com.ono.inssagram.databinding.FragmentInfoDefaultBinding
import com.softdough.grow.presentation.base.BaseFragment
import com.softdough.grow.util.ResourceUtil

class InfoFirstFragment : BaseFragment<FragmentInfoDefaultBinding>() {
    override val resourceId: Int = R.layout.fragment_info_default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.imageView.apply{
            background = context.getDrawable(R.drawable.info_image_1)
        }

        binding.textView.text = ResourceUtil(context!!).convertHtml(R.string.first_info_text)

        return binding.root
    }
}