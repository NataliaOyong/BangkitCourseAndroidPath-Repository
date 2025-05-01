package com.example.dicodingapp.ui.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingapp.data.response.ListEventsItem
import com.example.dicodingapp.data.response.EventResponse
import com.example.dicodingapp.data.retrofit.ApiConfig
import com.example.dicodingapp.databinding.FragmentSearchBinding
import com.example.dicodingapp.ui.EventAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(emptyList(), { event -> openEventDetail(event) }, useAlternateLayout = true)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = eventAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (isNetworkAvailable()) {
                    query?.let { fetchEvents(it) }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun fetchEvents(query: String) {
        val apiService = ApiConfig.getApiService()
        apiService.searchEvents(active = -1, query = query).enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val events = it.listEvents
                        displayResults(events)
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayResults(events: List<ListEventsItem>) {
        if (events.isEmpty()) {
            binding.resultsTextView.text = "No events found."
            binding.resultsTextView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.resultsTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            eventAdapter.updateEvents(events)
        }
    }

    private fun openEventDetail(event: ListEventsItem) {
        event.id?.let { eventId ->
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra("extra_event_id", eventId.toString())
            startActivity(intent)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
