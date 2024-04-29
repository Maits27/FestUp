package com.gomu.festup.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.util.Random;

private val md = MessageDigest.getInstance("SHA-512")


/**
 * Comparar hash con [String] y conversor de [String] a hash
 */

fun String.hash(): String{
    val messageDigest = md.digest(this.toByteArray())
    val no = BigInteger(1, messageDigest)
    var hashText = no.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}

fun String.compareHash(hash:String): Boolean{
    return this.hash() == hash
}

fun randomNum(): Int{
    val random = Random()
    return random.nextInt(9000000) + 1000000
}