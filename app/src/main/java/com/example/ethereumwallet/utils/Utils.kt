package com.example.ethereumwallet.utils

import java.util.regex.Pattern

class Utils {
    companion object {
        const val loadingGiffLink = "https://media4.giphy.com/media/hWZBZjMMuMl7sWe0x8/giphy.gif?cid=ecf05e4763vmg13w3oqh1lb4sc2brekr2z8v35gxp9rjlun9&rid=giphy.gif&ct=g"
        val ignoreCaseAddrPattern = Pattern.compile("^?[0-9a-fA-F]$")
        val lowerCaseAddrPattern = Pattern.compile("^?[0-9a-f]$")
        val upperCaseAddrPattern = Pattern.compile("^?[0-9A-F]$")
        const val infuraURL = "https://rinkeby.infura.io/v3/fb03c724108c40f3a2b202ea3a3243c4"
        const val REQUEST_IMAGE_CAPTURE = 1

    }
}