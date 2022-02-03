package com.sychev.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/*
Prints message to logs with 'XERTZ' TAG
 */
fun logXertz(message: Any?){
    Log.i("XERTZ", message.toString())
}

/*
Returns path from file in assets folder
 */
fun getAssetFilePath(context: Context, fileName: String): String {
    val file = File(context.filesDir, fileName)
    if (file.exists() && file.length() > 0) {
        return file.absolutePath
    }
    val inputStream: InputStream = context.assets.open(fileName)
    val outputStream: OutputStream = FileOutputStream(file)
    val buffer = ByteArray(4 * 1024)
    var read: Int = inputStream.read(buffer)
    while (read != -1) {
        outputStream.write(buffer, 0, read)
        read = inputStream.read(buffer)
    }
    outputStream.flush()
    return file.absolutePath
}

/*
Launches [content] in default scope
 */
fun defaultLaunch(content: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            content.invoke()
        }
}