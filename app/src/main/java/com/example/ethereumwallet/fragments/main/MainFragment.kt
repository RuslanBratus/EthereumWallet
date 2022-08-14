package com.example.ethereumwallet.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentMainBinding
import com.example.ethereumwallet.fragments.main.viewmodel.MainViewModel

class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var accountAddress : String
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        accountAddress = requireArguments().getString("address")!!

        viewModel = MainViewModel(accountAddress)

        Log.i("main", "before request data")

        viewModel.requestData()

        Log.i("main", "after request data")


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}