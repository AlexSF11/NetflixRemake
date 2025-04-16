package co.tiagoaguiar.netflixremake.util

import android.util.Log
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class CategoryTask {

    // Função vai executar uma chamada paralela

    fun execute(url: String) {
        // Nesse momento, estamos utilizando a UI-thread [1]
        val executor = Executors.newSingleThreadExecutor()

        executor.execute{
            try {
                // Nesse momento, estamos utilizando a NOVA-thread (Processo paralelo) [2]
                val requestURL = URL(url) // Abrir uma URL
                val urlConnection =
                    requestURL.openConnection() as HttpURLConnection // Abrir uma conexão
                urlConnection.readTimeout = 2000 // Tempo de leitura (2s)
                urlConnection.connectTimeout = 2000 // Tempo de conexão (2s)

                val statusCode: Int = urlConnection.responseCode
                if(statusCode > 400) {
                    throw  IOException("Erro na comunicação com o servidor!")
                }

                val stream = urlConnection.inputStream // Sequencia de bytes

                // Forma 1: simples e rápida
                //val jsonAsString = strem.bufferedReader().use { it.readText() } // Bytes -> Strings

                // Forma 2
                val buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                Log.i("Teste", jsonAsString)

            } catch (e: IOException) {
                Log.e("Teste", e.message ?: "Erro desconhecido", e)
            }
        }
    }

    private fun toString(stream: InputStream) : String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while(true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}