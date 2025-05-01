package com.example.myrecyclerview

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val imageView: ImageView = findViewById(R.id.profile_image)
        val nameTextView: TextView = findViewById(R.id.name_text_view)
        val emailTextView: TextView = findViewById(R.id.email_text_view)

        imageView.setImageResource(R.drawable.profile_picture)
        nameTextView.text = "Natalia Oyong"
        emailTextView.text = "a315b4kx3255@bangkit.academy"
    }
}
