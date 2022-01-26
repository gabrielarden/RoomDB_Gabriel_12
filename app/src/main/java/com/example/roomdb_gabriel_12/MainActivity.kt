package com.example.roomdb_gabriel_12

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdb_gabriel_12.room.Constant
import com.example.roomdb_gabriel_12.room.Movie
import com.example.roomdb_gabriel_12.room.MovieDB
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy { MovieDB(this) }
    lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()

    }

    override fun onStart(){
        super.onStart()
        loadMovie()
    }

    fun loadMovie(){
        CoroutineScope(Dispatchers.IO).launch {
            val movie = db.movieDao().getMovie()
            Log.d("MainActivity",  "dbresponse $movie")
            withContext(Dispatchers.Main){
                movieAdapter.setData(movie)
            }
        }
    }

    fun setupListener(){
        add_movie.setOnClickListener{
            intentAdd(0, Constant.TYPE_CREATE)
        }
    }

    fun intentAdd(movieId: Int, intentType: Int){
        startActivity(
            Intent(applicationContext, AddActivity::class.java)
                .putExtra("intent_id", movieId)
                .putExtra("intent_type", intentType)
        )
    }

    private fun setupRecyclerView(){
        movieAdapter = MovieAdapter(arrayListOf(), object : MovieAdapter.OnAdapterListener{
            override fun onClick(movie: Movie){
                intentAdd(movie.id,Constant.TYPE_READ)
            }
            override fun onUpdate(movie: Movie) {
                intentAdd(movie.id, Constant.TYPE_UPDATE)
            }
            override fun onDelete(movie: Movie) {
                deleteDialog(movie)
            }
        })
        rv_movie.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = movieAdapter
        }

    }
    private fun deleteDialog(movie: Movie) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin kah Min? ${movie.title}?")
            setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.movieDao().deleteMovie(movie)
                    loadMovie()
                }
            }
        }
        alertDialog.show()
    }
}