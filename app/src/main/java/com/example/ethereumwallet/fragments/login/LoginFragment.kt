package com.example.ethereumwallet.fragments.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentLoginBinding
import java.util.regex.Pattern


private val ignoreCaseAddrPattern = Pattern.compile("^?[0-9a-fA-F]$")
private val lowerCaseAddrPattern = Pattern.compile("^?[0-9a-f]$")
private val upperCaseAddrPattern = Pattern.compile("^?[0-9A-F]$")



class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding!!.addressInput.addTextChangedListener(MyTextWatcher(binding!!.addressInput))

        binding!!.loginButton.setOnClickListener {
            if (validateAccountAddress()) {
                val bundle = Bundle()
                bundle.putString("address", binding!!.addressInput.text.toString())
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)
            }
        }




//        CoroutineScope(Dispatchers.Default).launch {
//            Log.i("coroutines", "Line#41")
//            val googleMap1 = GlobalScope.async { getPreparedRequest() }
//            Log.i("coroutines", "Line#43")
//
//        }


    }

//    private suspend fun getPreparedRequest() {
//        val client = Web3j.build(
//            HttpService(
//                infuraURL
//            )
//        )
//
//
//
//
//
//        val credentials: Credentials = Credentials.create(ethAddress)
//
//
//        Log.i("eth","Balance: " + Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private inner class MyTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.addressInput -> validateAccountAddress()
            }
        }

    }

    private fun validateAccountAddress() : Boolean {
        val address = binding!!.addressInput.text.toString()

        return if (address.length != 64 || !ignoreCaseAddrPattern.matcher(address).find()) {
            binding!!.addressLayout.error = getString(R.string.error_ethereum_address)
            false
        }
        else if (lowerCaseAddrPattern.matcher(address).find()
            || upperCaseAddrPattern.matcher(address).find()) {
            binding!!.addressLayout.isErrorEnabled = false
            true
        }
        else {
            binding!!.addressLayout.error = getString(R.string.error_ethereum_address)
            false
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    //@TODO Thread realization save
    /*private fun withThreadRealization() {
        val thread = Thread {
            try {
                val client = Web3j.build(
                    HttpService(
                        infuraURL
                    )
                )



                val credentials: Credentials = Credentials.create(ethAddress)

                Log.i("eth", "private key = ${credentials.ecKeyPair.privateKey}")
                Log.i("eth", "public key = ${credentials.ecKeyPair.publicKey}")
                Log.i("eth", "ethadress = ${credentials.address}")
                //Log.i("eth", "ethaBalance = ${credentials.}")
                Log.i("eth","Balance: " + Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))
                //return Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER).toString()

                //Your code goes here
            } catch (e: Exception) {
                Log.i("eth", "ERROR! ${e.message} and ${e.cause}")
            }
        }
        thread.start()
    }*/


}