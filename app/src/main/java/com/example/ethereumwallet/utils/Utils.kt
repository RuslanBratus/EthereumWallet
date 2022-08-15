package com.example.ethereumwallet.utils

import java.util.regex.Pattern

class Utils {
    companion object {
        val ignoreCaseAddrPattern = Pattern.compile("^?[0-9a-fA-F]$")
        val lowerCaseAddrPattern = Pattern.compile("^?[0-9a-f]$")
        val upperCaseAddrPattern = Pattern.compile("^?[0-9A-F]$")
        const val infuraURL = "https://rinkeby.infura.io/v3/fb03c724108c40f3a2b202ea3a3243c4"

    }
}