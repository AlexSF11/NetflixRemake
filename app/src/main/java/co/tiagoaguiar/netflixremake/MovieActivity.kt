package co.tiagoaguiar.netflixremake

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie

class MovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val txtTitle: TextView = findViewById(R.id.movie_txt_title)
        val txtDesc: TextView = findViewById(R.id.movie_txt_desc)
        val txtCast: TextView = findViewById(R.id.movie_txt_cast)
        val rv: RecyclerView = findViewById(R.id.movie_rv_similar)

        txtTitle.text = "Batman Begins"
        txtDesc.text = "Essa é a descrição do filme do Batman"
        txtCast.text = getString(R.string.cast, "Ator A, AtorB, Ator C, Ator D")


        val movies = mutableListOf<Movie>()
        for (i in 0 until  15) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MovieAdapter(movies, R.layout.movie_item_similar)


        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        // Buscar o desenhavel (layer-list)
        val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable

        // Buscar o filme que eu quero
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4)

        // Atribuir a esse layer-list o novo filme
        layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)

        // Set no imageview
        val coverImg: ImageView = findViewById(R.id.movie_img)
        coverImg.setImageDrawable(layerDrawable)
    }
}