package com.example.ethereumwallet.fragments.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentLoginBinding
import com.example.ethereumwallet.utils.Utils.Companion.ignoreCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.lowerCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.upperCaseAddrPattern






class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding!!.privateKeyInput.addTextChangedListener(MyTextWatcher(binding!!.privateKeyInput))

        binding!!.loginButton.setOnClickListener {
            if (validateAccountAddress()) {
                val bundle = Bundle()
                bundle.putString("privateKey", binding!!.privateKeyInput.text.toString())
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)
            }
        }

    }

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
                R.id.privateKeyInput -> validateAccountAddress()
            }
        }

    }

    private fun validateAccountAddress() : Boolean {
        val address = binding!!.privateKeyInput.text.toString()

        return if (address.length != 64 || !ignoreCaseAddrPattern.matcher(address).find()) {
            binding!!.addressLayout.error = getString(R.string.wrong_private_key)
            false
        }
        else if (lowerCaseAddrPattern.matcher(address).find()
            || upperCaseAddrPattern.matcher(address).find()) {
            binding!!.addressLayout.isErrorEnabled = false
            true
        }
        else {
            binding!!.addressLayout.error = getString(R.string.wrong_private_key)
            false
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}