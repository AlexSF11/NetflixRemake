package co.tiagoaguiar.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.Buffer
import java.util.concurrent.Executors
import javax.security.auth.callback.Callback

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    val executor = Executors.newSingleThreadExecutor()
    interface  Callback {
        fun onPreExecute()
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
    }


    fun execute(url: String) {
        callback.onPreExecute()

        // Nesse momento, estamos utilizando a UI-thread [1]

        executor.execute{
            var urlConnection: HttpURLConnection? = null
            var stream: InputStream? = null
            try {
                // Nesse momento, estamos utilizando a NOVA-thread (Processo paralelo) [2]
                val requestURL = URL(url) // Abrir uma URL
                urlConnection = requestURL.openConnection() as HttpURLConnection // Abrir uma conexão
                urlConnection.readTimeout = 2000 // Tempo de leitura (2s)
                urlConnection.connectTimeout = 2000 // Tempo de conexão (2s)

                val statusCode: Int = urlConnection.responseCode
                if(statusCode > 400) {
                    throw  IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // Sequencia de bytes

                // Forma 1: simples e rápida
                val jsonAsString = stream.bufferedReader().use { it.readText() } // Bytes -> Strings

                val categories = toCategories(jsonAsString)

                handler.post {
                    // Aqui roda dentro da UI-thread
                    callback.onResult(categories)
                }

            } catch (e: IOException) {

                val message = e.message ?: "Erro desconhecido"
                Log.e("Teste", message, e)

                handler.post {
                    callback.onFailure(message)
                }

            } finally {
                urlConnection?.disconnect()
                stream?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String) : List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
    }

}