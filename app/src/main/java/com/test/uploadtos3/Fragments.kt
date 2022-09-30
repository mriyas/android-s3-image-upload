package com.test.uploadtos3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.test.uploadtos3.databinding.ActivityMainBinding

class Fragments : Fragment() {
    private val viewModel: S3UploadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val mBinding= ActivityMainBinding.inflate(layoutInflater)
        mBinding.act= requireActivity() as MainActivity?
        mBinding.lifecycleOwner=viewLifecycleOwner

        return super.onCreateView(inflater, container, savedInstanceState)
    }

}