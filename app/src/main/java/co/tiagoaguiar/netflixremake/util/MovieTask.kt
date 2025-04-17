package co.tiagoaguiar.netflixremake.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import co.tiagoaguiar.netflixremake.model.MovieDatail
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

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    val executor = Executors.newSingleThreadExecutor()
    interface  Callback {
        fun onPreExecute()
        fun onResult(movieDetail: MovieDatail)
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

                if (statusCode == 400) {
                    // Corrigir esse trecho de código

//                    stream = urlConnection.errorStream
//                    buffer = BufferedInputStream(stream)
//                    val jsonAsString = toString(buffer)

//                  val json = JSONObject(jsonAsString)
//                  val message = sjon.getString("message")
//                  throw IOException(message)


                } else if(statusCode > 400) {
                    throw  IOException("Erro na comunicação com o servidor!")
                }

                stream = urlConnection.inputStream // Sequencia de bytes

                // Forma 1: simples e rápida
                val jsonAsString = stream.bufferedReader().use { it.readText() } // Bytes -> Strings

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post {
                    // Aqui roda dentro da UI-thread
                    callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String) : MovieDatail {
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similars = mutableListOf<Movie>()

        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId, similarCoverUrl)
            similars.add(m)
        }

        val movie = Movie(id, coverUrl, title, desc, cast)
        return MovieDatail(movie, similars)
    }

}