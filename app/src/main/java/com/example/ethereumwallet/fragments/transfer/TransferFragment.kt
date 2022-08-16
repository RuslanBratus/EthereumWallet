package com.example.ethereumwallet.fragments.transfer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.valueIterator
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentTransferBinding
import com.example.ethereumwallet.fragments.transfer.viewmodel.TransferViewModel
import com.example.ethereumwallet.utils.Utils.Companion.ignoreCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.lowerCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.upperCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.loadingGiffLink
import com.example.ethereumwallet.utils.Utils.Companion.REQUEST_IMAGE_CAPTURE
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.*
import java.math.BigDecimal


class TransferFragment : Fragment() {
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding
    private lateinit var mViewModel: TransferViewModel
    private lateinit var privateKey: String
    private lateinit var detector : BarcodeDetector


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransferBinding.bind(view)

        privateKey = requireArguments().getString("privateKey")!!

        mViewModel = TransferViewModel(privateKey)
        binding!!.addressText.addTextChangedListener(MyTextWatcher(binding!!.addressText))
        binding!!.amountOfMoneyText.addTextChangedListener(MyTextWatcher(binding!!.amountOfMoneyText))

        binding!!.qrcodeScanImage.setOnClickListener {
            startCamera()
        }

        setData()





        binding!!.sendImage.setOnClickListener {
            if (validateSumOfMoney() && validateRecipientAddress()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                val sumOfETH = binding!!.amountOfMoneyText.text.toString().toBigDecimal()
                val recipientAccountAddress = binding!!.recipientAddressInput.text.toString()
                builder.setCancelable(true)
                    .setTitle("Transfer info:")
                    .setMessage("Sum of ETH: $sumOfETH\n\nRecipient address: $recipientAccountAddress")
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        transferETH(recipientAccountAddress = recipientAccountAddress, sumOfETH = sumOfETH)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildQrDetector()
    }

    private fun transferETH(recipientAccountAddress: String, sumOfETH: BigDecimal)  {


            //@TODO I should replays this trash with implementation another way to open new screen and don't kill coroutine processes
            //@TODO but I didn't find it so left it like that.

            binding!!.amountOfMoneyText.isGone = true
            binding!!.sumLayout.isGone = true
            binding!!.addressLayout .isGone = true
            binding!!.balanceText.isGone = true
            binding!!.recipientAddressInput.isGone = true
            binding!!.amountOfMoneyText.isGone = true
            binding!!.qrcodeScanImage .isGone = true
            binding!!.addressText.isGone = true
            binding!!.ethText.isGone = true


        Glide.with(requireContext())
            .load(loadingGiffLink)
            .override(300, 300)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding!!.sendImage)




        try {
            CoroutineScope(Dispatchers.Default).launch {
                val transferResult = GlobalScope.async {
                    mViewModel.transferETH(
                        recipientAccountAddress = recipientAccountAddress,
                        sumOfETH = sumOfETH
                    )
                }

                if (transferResult.await()) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Transfer was successful",
                            Toast.LENGTH_LONG
                        ).show()

                        val bundle = Bundle()

                        bundle.putString("privateKey", privateKey)
                        findNavController().navigate(
                            R.id.action_transferFragment_to_mainFragment,
                            bundle
                        )
                    }

                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Oops.. Something went wrong",
                            Toast.LENGTH_LONG
                        ).show()

                        val bundle = Bundle()
                        bundle.putString("privateKey", privateKey)
                        findNavController().navigate(
                            R.id.action_transferFragment_to_mainFragment,
                            bundle
                        )
                    }

                }
            }
        }
        catch (e: Exception) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Oops.. Something went wrong", Toast.LENGTH_LONG).show()
                val bundle = Bundle()
                bundle.putString("privateKey", privateKey)
                findNavController().navigate(R.id.action_transferFragment_to_mainFragment, bundle)
            }
        }




    }

    private fun setData() {

        mViewModel.requestData()
        setObservers()
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
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private inner class MyTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.amountOfMoneyText -> validateSumOfMoney()
                R.id.recipientAddressInput -> validateRecipientAddress()
            }
        }

    }

    private fun validateRecipientAddress(): Boolean {
        val address = binding!!.recipientAddressInput.text.toString()

        return if (address.length != 42 || !address.startsWith("0x") || !ignoreCaseAddrPattern.matcher(address).find()) {
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

    private fun validateSumOfMoney() : Boolean {
        val textMoney = binding!!.amountOfMoneyText.text.toString()
        val money : BigDecimal
        try {
            money = textMoney.toBigDecimal()
            if (money <= BigDecimal(0)) {
                binding!!.sumLayout.error = getString(R.string.error_sum_less_zero)
                return false
            }
            else if (money > mViewModel.balance.value) {
                binding!!.sumLayout.error = getString(R.string.error_bigger_sum_than_balance)
                return false
            }

            binding!!.sumLayout.isErrorEnabled = false
            return true
        }
        catch (e: Exception) {
            binding!!.sumLayout.error = getString(R.string.wrong_amount_of_eth)
            return false
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
            return
        }
        val frame: Frame = Frame.Builder().setBitmap(image).build()
        val barcodes: SparseArray<Barcode> = detector.detect(frame)
        barcodes.valueIterator().forEach { barcode ->
            binding!!.recipientAddressInput.setText(barcode.rawValue.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            detectQrCode(imageBitmap)
        }
    }


}