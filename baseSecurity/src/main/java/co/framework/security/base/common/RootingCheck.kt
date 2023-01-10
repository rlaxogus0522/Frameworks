package co.framework.security.base.common

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object RootingCheck {
    fun rootCheck(): Boolean { // 파일체크와 슈퍼유저 명령 둘중 하나라도 true떨어지면 루팅된거임
        return checkForSuBinary() || checkForBusyBoxBinary() || checkSuExists()
    }

    private fun checkForSuBinary(): Boolean {
        return checkForBinary(
            Utils.rootString[0]
        ) // function is available below
    }

    private fun checkForBusyBoxBinary(): Boolean {
        return checkForBinary(
            Utils.rootString[1]
        ) //function is available below
    }

    private fun checkSuExists(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime()
                .exec(arrayOf(Utils.rootString[2], Utils.rootString[0]))
            val `in` = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            val line = `in`.readLine()
            process.destroy()
            Utils.isRooted = line != null
            line != null
        } catch (e: Exception) {
            process?.destroy()
            Utils.isRooted = false
            false
        }
    }

    private fun checkForBinary(filename: String): Boolean {
        for (path in Utils.binaryPaths) {
            val f = File(path, filename)
            val fileExists = f.exists()
            if (fileExists) {
                Utils.isRooted = true
                return true
            }
        }
        Utils.isRooted = false
        return false
    }

}