package com.example.myrecyclerview

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Objects

class DetailActivity : AppCompatActivity() {

    companion object {
        const val key_planet = "extra_planet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(
            ColorDrawable(resources.getColor(R.color.colorPrimary))
        )

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = SpannableString(actionBar.title).apply {
                setSpan(ForegroundColorSpan(resources.getColor(android.R.color.white)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val dataPlanet = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra<Planet>(key_planet, Planet::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Planet>(key_planet)
        }

        if (dataPlanet != null) {
            val tvDetailName = findViewById<TextView>(R.id.tv_detail_name)
            val ivDetailPhoto = findViewById<ImageView>(R.id.iv_detail_photo)

            tvDetailName.text = dataPlanet.name
            ivDetailPhoto.setImageResource(dataPlanet.photo)

            showMoreDetails(dataPlanet)
        } else {
            Toast.makeText(this, "Data planet tidak ditemukan!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMoreDetails(dataPlanet: Planet) {
        val detailedDescriptions = resources.getStringArray(R.array.data_detail_description)
        val planetIndex = resources.getStringArray(R.array.data_name).indexOf(dataPlanet.name)
        val tvDetailedDescription = findViewById<TextView>(R.id.tv_detailed_description)

        if (planetIndex != -1) {
            tvDetailedDescription.text = detailedDescriptions[planetIndex]
        }
    }

    private fun sharePlanetDetails(dataPlanet: Planet) {
        val shareText = "Check out this planet: ${dataPlanet.name}\nDescription: ${dataPlanet.description}"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, "Share Planet Details"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_share -> {
                val dataPlanet = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra<Planet>(key_planet, Planet::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Planet>(key_planet)
                }
                if (dataPlanet != null) {
                    sharePlanetDetails(dataPlanet)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
