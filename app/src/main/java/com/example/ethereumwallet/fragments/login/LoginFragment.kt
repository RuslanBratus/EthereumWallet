package com.example.ethereumwallet.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ethereumwallet.R
import kotlinx.coroutines.*
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Convert.Unit ;

private const val infuraURL = "https://rinkeby.infura.io/v3/fb03c724108c40f3a2b202ea3a3243c4"
//private const val infuraURL = "https://mainnet.infura.io/v3/5d0cad45588d4eeca02e0b1d29a8e37a"
private const val ethAddress = "33cf1e58744993f3ef7e6d5153e0c1f6e4c484a547a3d2a1c7724af2ff01c2be"
//private const val ethAddress = "0ee4282d6b2b9673a9286fa2cd4d3563085ea507e0e6ea17dcd346f215131267"


class LoginFragment : Fragment() {
    private lateinit var client : Web3j


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        CoroutineScope(Dispatchers.Default).launch {
            Log.i("coroutines", "Line#41")
            val googleMap1 = GlobalScope.async { getPreparedRequest() }
            Log.i("coroutines", "Line#43")

        }

        Log.i("coroutines", "Line#53")

    }

    private suspend fun getPreparedRequest() {
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

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