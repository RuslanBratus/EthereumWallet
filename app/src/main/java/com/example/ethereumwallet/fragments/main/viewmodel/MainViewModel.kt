package com.example.ethereumwallet.fragments.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert.Unit
import org.web3j.utils.Convert
import java.math.BigDecimal
import com.example.ethereumwallet.utils.Utils.Companion.infuraURL


class MainViewModel(private val privateKey: String): ViewModel() {
    private lateinit var client : Web3j
    private lateinit var credentials: Credentials
    private var _balance = MutableLiveData(BigDecimal(0.0))
    val balance get() = _balance
    private lateinit var _address : String
    val address get() = _address

    fun requestData() = viewModelScope.launch {

        client  = Web3j.build(
            HttpService(
                infuraURL
            )
        )

        credentials = Credentials.create(privateKey)
        _address = credentials.address

        forTests()



        CoroutineScope(Dispatchers.Default).launch {
            GlobalScope.async { getBalance() }
        }


    }


    private fun getBalance() {
        _balance.postValue(Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))
    }

    private fun forTests() {
        Log.i("current user info", "credentials.address = ${credentials.address}")
    }


}