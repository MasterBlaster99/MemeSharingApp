package com.example.memesharingapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlin.math.max
import kotlin.math.min
import com.bumptech.glide.request.target.Target


class MainActivity : AppCompatActivity() {
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var imageView: ImageView
    var murl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.memeImageView)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        loadMeme()
    }
    fun loadMeme(){
        findViewById<Button>(R.id.sharebtn).isEnabled= false
        findViewById<Button>(R.id.nextbtn).isEnabled= false
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
// ...

// Instantiate the RequestQueue.

        val url = "https://meme-api.herokuapp.com/gimme"

// Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    murl = response.getString("url")

                    Glide.with(this).load(murl).listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                        ): Boolean {
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            findViewById<Button>(R.id.sharebtn).isEnabled= true
                            findViewById<Button>(R.id.nextbtn).isEnabled= true
                            return false
                        }

                        override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                        ): Boolean {
                            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            return false
                        }
                    }).into(imageView)
                },
                Response.ErrorListener {
                    findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                })

// Add the request to the RequestQueue.
        singleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            imageView.scaleX = scaleFactor
            imageView.scaleY = scaleFactor
            return true
        }
    }
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }
    fun shareMeme(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Checkout this cool meme $murl")
        val chooser = Intent.createChooser(intent, "share this meme using ...? ")
        startActivity(chooser)
    }
    fun nextMeme(view: View) {
        loadMeme()
    }
}