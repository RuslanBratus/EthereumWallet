package com.example.ethereumwallet.fragments.transfer

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentTransferBinding
import com.example.ethereumwallet.fragments.transfer.viewmodel.TransferViewModel
import com.example.ethereumwallet.utils.Utils.Companion.ignoreCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.lowerCaseAddrPattern
import com.example.ethereumwallet.utils.Utils.Companion.upperCaseAddrPattern
import java.math.BigDecimal


class TransferFragment : Fragment() {
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding
    private lateinit var mViewModel: TransferViewModel
    private lateinit var accountAddress: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransferBinding.bind(view)

        accountAddress = requireArguments().getString("privateKey")!!

        mViewModel = TransferViewModel(accountAddress)

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
                        mViewModel.transferETH(
                            recipientAccountAddress = recipientAccountAddress,
                            sumOfETH = sumOfETH
                        )
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }

                val dialog: AlertDialog = builder.create()
                dialog.show()
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
        var money : BigDecimal
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

}