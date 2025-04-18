package com.example.restaurantreview.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.restaurantreview.R
import com.example.restaurantreview.data.response.CustomerReviewsItem
import com.example.restaurantreview.data.response.PostReviewResponse
import com.example.restaurantreview.data.response.Restaurant
import com.example.restaurantreview.data.response.RestaurantResponse
import com.example.restaurantreview.data.retrofit.ApiConfig
import com.example.restaurantreview.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
//    companion object {
//        private const val TAG = "MainActivity"
//        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        //bagian ini ditambahkan saat mainviewmodel
//        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.restaurant.observe(this) { restaurant ->
            setRestaurantData(restaurant)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        //bagian ini ditambahkan saat mainviewmodel
        mainViewModel.listReview.observe(this) { consumerReviews ->
            setReviewData(consumerReviews)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.snackbarText.observe(this, {
            it.getContentIfNotHandled()?.let { snackBarText ->
            Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        })

        binding.btnSend.setOnClickListener { view ->
            mainViewModel.postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //dipindahkan ke mainviewmodel
//    private fun findRestaurant() {
//        showLoading(true)
//        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
//        client.enqueue(object : Callback<RestaurantResponse> {
//            override fun onResponse(
//                call: Call<RestaurantResponse>,
//                response: Response<RestaurantResponse>
//            ) {
//                showLoading(false)
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        setRestaurantData(responseBody.restaurant)
//                        setReviewData(responseBody.restaurant.customerReviews)
//                    }
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
//                showLoading(false)
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }

    private fun setRestaurantData(restaurant: Restaurant) {
        binding.tvTitle.text = restaurant.name
        binding.tvDescription.text = restaurant.description
        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }
    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        val adapter = ReviewAdapter()
        adapter.submitList(consumerReviews)
        binding.rvReview.adapter = adapter
        binding.edReview.setText("")
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    //dipindahkan ke mainviewmodel
//    private fun postReview(review: String) {
//        showLoading(true)
//        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review)
//        client.enqueue(object : Callback<PostReviewResponse> {
//            override fun onResponse(
//                call: Call<PostReviewResponse>,
//                response: Response<PostReviewResponse>
//            ) {
//                showLoading(false)
//                val responseBody = response.body()
//                if (response.isSuccessful && responseBody != null) {
//                    setReviewData(responseBody.customerReviews)
//                } else {
//                    Log.e(TAG, "onFailure: ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
//                showLoading(false)
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }
}