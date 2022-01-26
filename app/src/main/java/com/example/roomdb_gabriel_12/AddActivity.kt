package com.example.roomdb_gabriel_12

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.roomdb_gabriel_12.room.Constant
import com.example.roomdb_gabriel_12.room.Movie
import com.example.roomdb_gabriel_12.room.MovieDB
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class AddActivity : AppCompatActivity() {

    val db by lazy { MovieDB(this) }
    private var movieId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setupView()
        setupListener()
        movieId = intent.getIntExtra("intent_id", 0)

        //Toast.makeText(this, movieId.toString(), Toast.LENGTH_SHORT).show()
    }

    fun setupView(){
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val intentType = intent.getIntExtra("intent_type", 0)
        when (intentType) {
            Constant.TYPE_CREATE -> {
                btn_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                btn_save.visibility = View.GONE
                btn_update.visibility = View.GONE
                getMovie()
            }
            Constant.TYPE_UPDATE -> {
                btn_save.visibility = View.GONE
                getMovie()
            }
        }
    }

    fun setupListener(){
        btn_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.movieDao().addMovie(
                    Movie(0,et_title.text.toString(), et_description.text.toString())
                )
                finish()
            }
        }
        btn_update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.movieDao().updateMovie(
                    Movie(movieId,et_title.text.toString(),
                        et_description.text.toString())
                )
                finish()
            }
        }
    }

    fun getMovie(){
        movieId = intent.getIntExtra("intent_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.movieDao().getMovie( movieId ) [0]
            et_title.setText( movies.title )
            et_description.setText( movies.desc )
        }
    }

    //override fun onSupportNavigateUp(): Boolean {
    //onBackPressed()
    //return super.onSupportNavigateUp()}
}

