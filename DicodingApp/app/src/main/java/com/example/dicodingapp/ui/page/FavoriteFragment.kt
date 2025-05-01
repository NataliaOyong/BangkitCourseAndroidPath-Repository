package com.example.dicodingapp.ui.page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingapp.data.response.ListEventsItem
import com.example.dicodingapp.databinding.FragmentFavoriteBinding
import com.example.dicodingapp.di.Injection
import com.example.dicodingapp.ui.EventAdapter
import com.example.dicodingapp.ui.viewmodel.FavoriteViewModel
import com.example.dicodingapp.ui.factory.FavoriteViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter(listOf(), { event -> openEventDetail(event) })
        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }

        favoriteViewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(Injection.provideFavoriteEventRepository(requireContext()))
        )[FavoriteViewModel::class.java]

        favoriteViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favorites?.let {
                eventAdapter.updateEvents(it.map { favoriteEvent ->
                    ListEventsItem(
                        id = favoriteEvent.id,
                        name = favoriteEvent.name,
                        imageLogo = favoriteEvent.imageUrl
                    )
                })
                binding.progressBar.visibility = View.GONE
            }
        }

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
