package com.example.dicodingapp.ui.page

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingapp.data.response.ListEventsItem
import com.example.dicodingapp.databinding.FragmentFinisedBinding
import com.example.dicodingapp.ui.EventAdapter
import com.example.dicodingapp.ui.viewmodel.EventViewModel

class FinisedFragment : Fragment() {

    private var _binding: FragmentFinisedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter
    private var isNoInternetToastShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinisedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(listOf(), { event -> openEventDetail(event) }, useAlternateLayout = true)

        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { eventList ->
            if (eventList != null && eventList.isNotEmpty()) {
                Log.d("FinisedFragment", "Received events: $eventList")
                eventAdapter.updateEvents(eventList)
                binding.rvEvents.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                isNoInternetToastShown = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvEvents.visibility = View.GONE
                if (!isNoInternetToastShown) {
                    Toast.makeText(context, "No events found", Toast.LENGTH_SHORT).show()
                    isNoInternetToastShown = true
                }
            }
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.progressBar.visibility = View.GONE
                if (!isNoInternetToastShown) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    isNoInternetToastShown = true
                }
            }
        }

        eventViewModel.fetchFinishedEvents(requireContext())
        binding.progressBar.visibility = View.VISIBLE
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
}
