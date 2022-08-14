package com.example.ethereumwallet.fragments.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert.Unit
import org.web3j.utils.Convert

private const val infuraURL = "https://rinkeby.infura.io/v3/fb03c724108c40f3a2b202ea3a3243c4"

class MainViewModel(private val ethAddress: String): ViewModel() {
    ////@TODO private lateinit var accountInfoAndOthers : EthereumAccount
    //@TODO Live Data + observe from Fragment to load data in proper view

    fun requestData() = viewModelScope.launch {
        //@TODO Тут поставити 1 клас, який вміщуватиме усю інфу про наш аккаунт (Глянути, можливо уже є готові класи від web3j
        //@TODO Також глянути, чи отримуємо ми за допомогою getBalance WEI чи GWEI! Відповідь: Залежить від метода. чекнути на сайті.

        CoroutineScope(Dispatchers.Default).launch {
            Log.i("coroutines", "Line#41")
            val balance = GlobalScope.async { getBalance() }
            //GlobalScope.async { whileForever() }
            Log.i("coroutines", "Line#43")

        }


    }


    private fun getBalance() {
        val client = Web3j.build(
            HttpService(
                infuraURL
            )
        )

        val credentials: Credentials = Credentials.create(ethAddress)


        Log.i("eth","Balance: " + Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))

    }

}