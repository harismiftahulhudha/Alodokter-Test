package co.harismiftahulhudha.alodoktertest.helpers

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FormatStringHelper {
    companion object {
        fun getMD5(input: String): String {
            return try {

                val md: MessageDigest = MessageDigest.getInstance("MD5")
                val messageDigest: ByteArray = md.digest(input.toByteArray())
                val no = BigInteger(1, messageDigest)
                var hashtext: String = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                hashtext
            }
            catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
    }
}