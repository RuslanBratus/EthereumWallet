package com.example.ethereumwallet.fragments.transfer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ethereumwallet.utils.Utils.Companion.infuraURL
import kotlinx.coroutines.*
import org.web3j.abi.datatypes.Bool
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
import java.util.*


class TransferViewModel(private val privateKey: String): ViewModel() {
    private lateinit var client : Web3j
    private lateinit var credentials: Credentials
    private var _balance = MutableLiveData(BigDecimal(0.0))
    val balance get() = _balance
//    private lateinit var _address : String
//    val address get() = _address

    fun requestData() = viewModelScope.launch {

        client  = Web3j.build(
            HttpService(
                infuraURL
            )
        )

        credentials = Credentials.create(privateKey)
        //_address = credentials.address
        CoroutineScope(Dispatchers.Default).launch {
            GlobalScope.async { getBalance() }
        }


    }


    private fun getBalance() {
        _balance.postValue(Convert.fromWei(client.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toString(), Convert.Unit.ETHER))
    }

    fun transferETH(recipientAccountAddress: String, sumOfETH: BigDecimal) {

        CoroutineScope(Dispatchers.Default).launch {
            //@TODO TRY CATCH! THROWS EVEN! catch in fragment

            val ethGetTransactionCount = async { getTransactionCount() }
            //Log.i("transferETH", "nonce = $ethGetTransactionCount")
            val nonce: BigInteger =  ethGetTransactionCount.await().transactionCount;
            val value: BigInteger = Convert.toWei(sumOfETH, Unit.ETHER).toBigInteger()


            // Gas Parameters
            val gasLimit: BigInteger = BigInteger.valueOf(21000)
            val gasPrice: BigInteger = Convert.toWei("1", Unit.GWEI).toBigInteger()


            // Prepare the rawTransaction
            val rawTransaction = RawTransaction.createEtherTransaction(
                nonce,
                gasPrice,
                gasLimit,
                recipientAccountAddress,
                value
            )

            Log.i("transferETH", "rawTransaction = $rawTransaction")

            // Sign the transaction
            val signedMessage = async { TransactionEncoder.signMessage(rawTransaction, credentials) }
            val hexValue  = Numeric.toHexString(signedMessage.await())


            // Send transaction
            val ethSendTransaction = async { sendRawTransaction(hexValue) }
            val transactionHash = ethSendTransaction.await().transactionHash
            println("transactionHash: $transactionHash")

            // Wait for transaction to be mined

            // Wait for transaction to be mined

            val transactionReceipt = async { getTransactionMining(transactionHash) }
            transactionReceipt.start()






        }

        // Get the latest nonce
        //Log.i("transferETH", "transferETH started")
        //val ethGetTransactionCount: EthGetTransactionCount = client.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send()
//        Log.i("transferETH", "nonce = $ethGetTransactionCount")
//        val nonce: BigInteger =  ethGetTransactionCount.transactionCount;
//        Log.i("transferETH", "nonce = $nonce")

        // Value to transfer (in wei)
        // Value to transfer (in wei)




    }

    private fun getTransactionMining(transactionHash: String): Boolean {
        var transactionReceipt: Optional<TransactionReceipt?>? = null

        Log.i("getTransactionMining", "getTransactionMining")
        do {
            println("checking if transaction $transactionHash is mined....")
            val ethGetTransactionReceiptResp: EthGetTransactionReceipt =
                client.ethGetTransactionReceipt(transactionHash).send()
            transactionReceipt = ethGetTransactionReceiptResp.transactionReceipt
            Log.i("getTransactionMining", "transactionReceipt = $transactionReceipt")
            Thread.sleep(3000) // Wait 3 sec
        //} while (!transactionReceipt.isPresent()) @TODO Use API 24.
        } while (transactionReceipt == null)

        Log.i("getTransactionMining", "getTransactionMining END!")
//        Log.i("getTransactionMining",
//            "Transaction $transactionHash was mined in block # " + transactionReceipt.get()
//                .getBlockNumber()
//        )
        Log.i("getTransactionMining",
            "Balance: " + Convert.fromWei(
                client.ethGetBalance(
                    credentials.address,
                    DefaultBlockParameterName.LATEST
                ).send().balance.toString(), Unit.ETHER
            )
        )
        return true
    }

    private fun sendRawTransaction(hexValue: String): EthSendTransaction {
        Log.i("sendRawTransaction", "sendRawTransaction")
        return client.ethSendRawTransaction(hexValue).send()
    }

    private fun getTransactionCount(): EthGetTransactionCount {
        Log.i("getTransactionCount", "getTransactionCount")
        return client.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST).send()
    }


}



