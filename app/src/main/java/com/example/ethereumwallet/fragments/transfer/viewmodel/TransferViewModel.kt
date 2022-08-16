package com.example.ethereumwallet.fragments.transfer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ethereumwallet.utils.Utils.Companion.infuraURL
import kotlinx.coroutines.*
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import org.web3j.utils.Convert.Unit
import java.io.IOException
import java.util.*


class TransferViewModel(private val privateKey: String): ViewModel() {
    private lateinit var client : Web3j
    private lateinit var credentials: Credentials
    private var _balance = MutableLiveData(BigDecimal(0.0))
    val balance get() = _balance

    fun requestData() = viewModelScope.launch {

        client  = Web3j.build(
            HttpService(
                infuraURL
            )
        )

        credentials = Credentials.create(privateKey)
        CoroutineScope(Dispatchers.Default).launch {
            GlobalScope.async { getBalance() }
        }


    }


    private fun getBalance() {
        _balance.postValue(Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Unit.ETHER))
    }

    @Throws(IOException::class)
    fun transferETH(recipientAccountAddress: String, sumOfETH: BigDecimal) : Boolean = runBlocking {
    //fun transferETH(callBack : TransferCallBack, recipientAccountAddress: String, sumOfETH: BigDecimal)  {

        //CoroutineScope(Dispatchers.Default).launch {

            val ethGetTransactionCount = async { getTransactionCount() }
            val nonce: BigInteger = ethGetTransactionCount.await().transactionCount
            val value: BigInteger = Convert.toWei(sumOfETH, Unit.ETHER).toBigInteger()


            // Gas Parameters
            val gasLimit: BigInteger = BigInteger.valueOf(21000)
            val gasPrice: BigInteger = Convert.toWei("20", Unit.GWEI).toBigInteger()


            // Prepare the rawTransaction
            val rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                gasPrice,
                gasLimit,
                recipientAccountAddress,
                value
            )


            // Sign the transaction
            val signedMessage =
                async { TransactionEncoder.signMessage(rawTransaction, credentials) }
            val hexValue = Numeric.toHexString(signedMessage.await())

            // Send transaction
            val ethSendTransaction = async { sendRawTransaction(hexValue) }
            val transactionHash = ethSendTransaction.await().transactionHash


            // Wait for transaction to be mined
            val isTransactionSuccessful = async { getTransactionMining(transactionHash) }

        return@runBlocking isTransactionSuccessful.await()
    }

    private fun getTransactionMining(transactionHash: String): Boolean {
        var transactionReceipt: Optional<TransactionReceipt?>?


        while (true) {
                val ethGetTransactionReceiptResp: EthGetTransactionReceipt =
                    client.ethGetTransactionReceipt(transactionHash).send()
                transactionReceipt = ethGetTransactionReceiptResp.transactionReceipt
                if (transactionReceipt != null) {
                    if (!transactionReceipt.isPresent) {
                        return true
                    }
                }
                Thread.sleep(3000)
            }


    }

    private fun sendRawTransaction(hexValue: String): EthSendTransaction {
        return client.ethSendRawTransaction(hexValue).send()
    }

    private fun getTransactionCount(): EthGetTransactionCount {
        return client.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send()
    }


}



