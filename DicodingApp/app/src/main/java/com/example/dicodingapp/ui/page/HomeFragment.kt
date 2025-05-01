package com.example.dicodingapp.ui.page

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingapp.data.response.ListEventsItem
import com.example.dicodingapp.databinding.FragmentHomeBinding
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.dicodingapp.ui.EventAdapter
import com.example.dicodingapp.ui.viewmodel.EventViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private lateinit var upcomingEventAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upcomingEventAdapter = EventAdapter(listOf(), { event -> openEventDetail(event) }, useAlternateLayout = false)
        finishedEventAdapter = EventAdapter(listOf(), { event -> openEventDetail(event) }, useAlternateLayout = true)

        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingEventAdapter
        }

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedEventAdapter
        }

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        eventViewModel.activeEvents.observe(viewLifecycleOwner) { eventList ->
            upcomingEventAdapter.updateEvents(eventList)
            binding.rvUpcomingEvents.visibility = if (eventList.isEmpty()) View.GONE else View.VISIBLE
        }

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { eventList ->
            finishedEventAdapter.updateEvents(eventList)
            binding.rvFinishedEvents.visibility = if (eventList.isEmpty()) View.GONE else View.VISIBLE
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.progressBar.visibility = View.VISIBLE

        if (isInternetAvailable(requireContext())) {
            eventViewModel.fetchActiveEvents(requireContext())
            eventViewModel.fetchFinishedEvents(requireContext())
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEventDetail(event: ListEventsItem) {
        event.id?.let { eventId ->
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra("extra_event_id", eventId.toString())
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
