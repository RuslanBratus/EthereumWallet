package com.example.ethereumwallet.fragments.login.viewmodel

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Convert.Unit

//private const val infuraURL = "https://rinkeby.infura.io/v3/fb03c724108c40f3a2b202ea3a3243c4"
private const val infuraURL = "https://mainnet.infura.io/v3/5d0cad45588d4eeca02e0b1d29a8e37a"
private const val ethAddress = "33cf1e58744993f3ef7e6d5153e0c1f6e4c484a547a3d2a1c7724af2ff01c2be"

class LoginViewModel : ViewModel() {
    private var balance : EthGetBalance? = null
    private val responseManager = ResponseManager()


    fun getBalance() = viewModelScope.launch {
        val thread = Thread {
            try {
                val client = Web3j.build(
                    HttpService(
                        infuraURL
                    )
                )

                val ethAddress = "33cf1e58744993f3ef7e6d5153e0c1f6e4c484a547a3d2a1c7724af2ff01c2be"

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
        //getBalanceHere()
    }


    private suspend fun getBalanceHere() {











    }


}

class ResponseManager: AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String {

        val client = Web3j.build(
            HttpService(
                infuraURL
            )
        )

        val ethAddress = "33cf1e58744993f3ef7e6d5153e0c1f6e4c484a547a3d2a1c7724af2ff01c2be"

        val credentials: Credentials = Credentials.create(ethAddress)

        Log.i("eth", "private key = ${credentials.ecKeyPair.privateKey}")
        Log.i("eth", "public key = ${credentials.ecKeyPair.publicKey}")
        Log.i("eth", "ethadress = ${credentials.address}")
        //Log.i("eth", "ethaBalance = ${credentials.}")
        Log.i("eth","Balance: " + Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))
        //return Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER).toString()


        return ""
    }

}