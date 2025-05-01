package com.example.myrecyclerview

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecyclerview.ListPlanetAdapter.OnItemClickCallback
import java.util.Objects

class MainActivity : AppCompatActivity() {

    private lateinit var rvPlanets: RecyclerView
    private val list = ArrayList<Planet>()
    private var isGridMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Objects.requireNonNull(supportActionBar)?.setBackgroundDrawable(
            ColorDrawable(resources.getColor(R.color.colorPrimary))
        )

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = SpannableString(actionBar.title).apply {
                setSpan(ForegroundColorSpan(resources.getColor(android.R.color.white)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvPlanets = findViewById(R.id.rv_planets)
        rvPlanets.setHasFixedSize(true)

        list.addAll(getListPlanets())
        showRecyclerView()
    }

    private fun getListPlanets(): ArrayList<Planet> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        return ArrayList<Planet>().apply {
            for (i in dataName.indices) {
                add(Planet(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1)))
            }
            dataPhoto.recycle()
        }
    }

    private fun showRecyclerView() {
        rvPlanets.layoutManager = if (isGridMode) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }

        val listPlanetAdapter = ListPlanetAdapter(list, isGridMode).apply {
            setOnItemClickCallback(object : OnItemClickCallback {
                override fun onItemClicked(data: Planet) {
                    val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
                    intentDetail.putExtra(DetailActivity.key_planet, data)
                    startActivity(intentDetail)
                }
            })
        }
        rvPlanets.adapter = listPlanetAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_list -> {
                isGridMode = false
                showRecyclerView()
                true
            }
            R.id.action_grid -> {
                isGridMode = true
                showRecyclerView()
                true
            }
            R.id.about_page -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
