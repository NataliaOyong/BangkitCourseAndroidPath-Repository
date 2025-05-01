package com.example.dicodingapp.ui.page

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dicodingapp.R
import com.example.dicodingapp.data.FavoriteEventRepository
import com.example.dicodingapp.data.local.entity.FavoriteEvent
import com.example.dicodingapp.data.response.DetailEventResponse
import com.example.dicodingapp.data.response.EventDetail
import com.example.dicodingapp.databinding.ActivityEventDetailBinding
import com.example.dicodingapp.ui.viewmodel.EventDetailViewModel
import com.example.dicodingapp.ui.factory.EventDetailViewModelFactory
import com.example.dicodingapp.data.local.room.FavoriteEventDatabase

@Suppress("DEPRECATION")
class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val eventDetailViewModel: EventDetailViewModel by viewModels {
        EventDetailViewModelFactory(
            FavoriteEventRepository.getInstance(
                FavoriteEventDatabase.getDatabase(application).favoriteEventDao()
            )
        )
    }

    private var isFavorite: Boolean = false
    private val currentEventId: Int by lazy { intent.getStringExtra(EXTRA_EVENT_ID)?.toInt() ?: -1 }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("EventDetailActivity", "Received event ID: $currentEventId")

        if (currentEventId != -1) {
            showLoading(true)
            eventDetailViewModel.checkIfFavorite(currentEventId)

            eventDetailViewModel.isFavorite.observe(this) { isFav ->
                Log.d("EventDetailActivity", "Favorite status retrieved: $isFav")
                isFavorite = isFav
                updateFavoriteIcon()
            }

            eventDetailViewModel.getEventDetail(currentEventId.toString())
        } else {
            Log.w("EventDetailActivity", "Event ID is invalid")
        }

        eventDetailViewModel.eventDetail.observe(this) { detailEventResponse ->
            detailEventResponse?.let {
                showLoading(false)
                displayEventDetail(it)
            } ?: run {
                isFavorite = false
                updateFavoriteIcon()
                Log.w("EventDetailActivity", "Event detail is null")
            }
        }

        binding.buttonRegister.setOnClickListener {
            eventDetailViewModel.eventDetail.value?.event?.link?.let { link ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
            }
        }

        binding.imageViewFavorite.setOnClickListener {
            eventDetailViewModel.eventDetail.value?.event?.let { event ->
                if (!isFavorite) {
                    event.toFavoriteEvent().let { favoriteEvent ->
                        eventDetailViewModel.addFavorite(favoriteEvent)
                        isFavorite = true
                        Log.d("EventDetailActivity", "Added to favorites: ${event.id}")
                    }
                } else {
                    event.id?.let { eventId ->
                        eventDetailViewModel.removeFavorite(eventId)
                        isFavorite = false
                        Log.d("EventDetailActivity", "Removed from favorites: $eventId")
                    }
                }
                updateFavoriteIcon()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun displayEventDetail(detailEventResponse: DetailEventResponse) {
        detailEventResponse.event?.let { event ->
            Glide.with(this)
                .load(event.mediaCover)
                .into(binding.imageViewEvent)

            binding.textViewEventName.text = event.name ?: "Event Name Unavailable"
            supportActionBar?.title = event.name ?: "Event Details"
            binding.textViewEventOrganizer.text = event.ownerName ?: "Organizer Unknown"
            binding.textViewEventDate.text = event.beginTime ?: "No Date Available"

            val remainingQuota = (event.quota ?: 0) - (event.registrants ?: 0)
            binding.textViewEventQuota.text = "Sisa Kuota: $remainingQuota"

            binding.textViewEventDescription.text = fromHtml(event.description)
        } ?: Log.w("EventDetailActivity", "Event is null")
    }

    private fun fromHtml(html: String?): CharSequence {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html ?: "", Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html ?: "")
        }
    }

    private fun updateFavoriteIcon() {
        Log.d("EventDetailActivity", "Updating favorite icon: isFavorite = $isFavorite")
        binding.imageViewFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

fun EventDetail.toFavoriteEvent(): FavoriteEvent {
    return FavoriteEvent(
        id = this.id ?: 0,
        name = this.name ?: "N/A",
        imageUrl = this.mediaCover ?: "",
        description = this.description ?: "No description available"
    )
}
