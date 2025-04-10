package co.tiagoaguiar.netflixremake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movies = mutableListOf<Movie>()
        for(i in 0 until 60) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        // Filmes - Lista vertical
            // Categoria 1
                // Lista Horizontal: Filme1 - Filme2 - Filme3 - Filme4...
            // Categoria 2
                // Lista Horizontal: Filme1 - Filme2 - Filme3 - Filme4...
            // Categorias 3
                // Lista Horizontal: Filme1 - Filme2 - Filme3 - Filme4...

        val categories = mutableListOf<Category>()
        for (j in 0 until 10) {
            val movies = mutableListOf<Movie>()
            for (i in 0 until  15) {
                val movie = Movie(R.drawable.movie)
                movies.add(movie)
            }
            val category = Category("cat $j", movies)
            categories.add(category)
        }

        // Na vertical a lista (MainAdapter) de categorias
        // E dentro de cada item [TextView+RecyclerView horizontal]
        // (cada categoria) teremos
        // Uma lista (MovieAdapter) de filmes (ImageView)
        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById((R.id.rv_main))
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }



}