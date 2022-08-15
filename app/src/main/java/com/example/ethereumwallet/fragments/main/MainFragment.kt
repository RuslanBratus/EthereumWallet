package com.example.ethereumwallet.fragments.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentMainBinding
import com.example.ethereumwallet.fragments.main.viewmodel.MainViewModel
import java.math.BigDecimal


class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var privateKey : String
    private lateinit var mViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        privateKey = requireArguments().getString("privateKey")!!

        mViewModel = MainViewModel(privateKey)

        binding!!.copyAddressImage.setOnClickListener {
            val clipboard: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", mViewModel.address)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Address copied successfully", Toast.LENGTH_SHORT).show()
        }

        binding!!.sendImage.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("privateKey", privateKey)
                findNavController().navigate(R.id.action_mainFragment_to_transferFragment, bundle)
        }


        setData()




    }

    private fun setData() {

        mViewModel.requestData()
        setObservers()
        setAddress()


    }

    private fun setAddress() {
        val address = mViewModel.address
        val formattedAddress = (address.subSequence(0, 5).toString() + "..." + address.subSequence(
            address.length - 5,
            address.length - 1
        ))

        binding!!.addressText.text = formattedAddress

    }

    private fun setObservers() {



        val balanceObserver = Observer<BigDecimal> { balance ->
            //@TODO Find proper format for proper PRINT of ETH Balance
            //@TODO replace deprecated code
            val current = resources.configuration.locale
            binding!!.balanceText.text = String.format(current, "%,f", balance)
            //val format = DecimalFormat("#.#########")
            // binding!!.balanceText.text = format.format(balance) @TODO use this to burn not needed '0' in numbers, but we lose String.format..
        }

        mViewModel.balance.observe(viewLifecycleOwner, balanceObserver)

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