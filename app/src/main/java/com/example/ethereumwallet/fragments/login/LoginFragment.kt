package com.example.ethereumwallet.fragments.login

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentLoginBinding
import com.example.ethereumwallet.utils.Utils.Companion.ignoreCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.lowerCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.upperCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.REQUEST_IMAGE_CAPTURE
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector




class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding
    private lateinit var detector : BarcodeDetector


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding!!.privateKeyInput.addTextChangedListener(MyTextWatcher(binding!!.privateKeyInput))


        binding!!.qrcodeScanImage.setOnClickListener {
            startCamera()
        }

        binding!!.loginButton.setOnClickListener {
            if (validateAccountAddress()) {
                saveAccountPrivateKeyInPreferences()
                val bundle = Bundle()
                bundle.putString("privateKey", binding!!.privateKeyInput.text.toString())
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)
            }
        }
    }

    private fun startCamera() {
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun buildQrDetector() {
        detector = BarcodeDetector.Builder(requireContext())
            .setBarcodeFormats(Barcode.QR_CODE)
//            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()


    }



    private fun detectQrCode(image: Bitmap) {
        if(!detector.isOperational){
            Log.e("detectQrCode", "Could not setup detector")
            return
        }
        val frame: Frame = Frame.Builder().setBitmap(image).build()
        val barcodes: SparseArray<Barcode> = detector.detect(frame)
        barcodes.valueIterator().forEach { barcode ->
            binding!!.privateKeyInput.setText(barcode.rawValue.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //imageView.setImageBitmap(imageBitmap)
            detectQrCode(imageBitmap)
        }
    }


    private fun saveAccountPrivateKeyInPreferences() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.private_key), binding!!.privateKeyInput.text.toString())
            apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getPreferenceAddress()
        return inflater.inflate(R.layout.fragment_login, container, false)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildQrDetector()
    }

    private fun getPreferenceAddress() {
        val sharedPrefAccountAddress = requireActivity().getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.private_key), null)
        if (sharedPrefAccountAddress != null) {
            val bundle = Bundle()
            bundle.putString("privateKey", sharedPrefAccountAddress)
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment, bundle)
        }


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