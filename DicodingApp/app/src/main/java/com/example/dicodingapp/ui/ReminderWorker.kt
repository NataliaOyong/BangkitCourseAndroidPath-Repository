package com.example.dicodingapp.ui

import android.app.NotificationManager
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingapp.R
import com.example.dicodingapp.data.response.DetailEventResponse
import com.example.dicodingapp.data.response.EventDetail
import com.example.dicodingapp.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return Result.failure()
            }
        }

        val eventId = getClosestEventId() ?: return Result.success()
        getEventDetail(eventId) { eventDetail ->
            eventDetail?.let { showNotification(it) }
        }
        return Result.success()
    }

    private fun getClosestEventId(): Int? {
        return 1
    }

    private fun getEventDetail(eventId: Int, callback: (EventDetail?) -> Unit) {
        val call = ApiConfig.getApiService().getEventDetail(eventId.toString())
        call.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                if (response.isSuccessful) {
                    callback(response.body()?.event)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun showNotification(event: EventDetail) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "reminder_channel")
            .setContentTitle("Upcoming Event: ${event.name}")
            .setContentText("Starts at: ${event.beginTime}")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(event.description ?: "No description available."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(event.id ?: 0, notification)
    }

    companion object {
        const val WORK_NAME = "DailyReminderWorker"
    }
}
