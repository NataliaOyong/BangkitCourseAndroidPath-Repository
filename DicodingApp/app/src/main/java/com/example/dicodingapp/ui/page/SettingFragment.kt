package com.example.dicodingapp.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dicodingapp.R
import com.example.dicodingapp.ui.SettingPreferences
import com.example.dicodingapp.ui.dataStore
import com.example.dicodingapp.ui.viewmodel.ThemeViewModel
import com.example.dicodingapp.ui.factory.ThemeViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.dicodingapp.ui.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private lateinit var switchTheme: SwitchMaterial
    private lateinit var switchDailyReminder: SwitchMaterial
    private lateinit var themeViewModel: ThemeViewModel
    private lateinit var settingPreferences: SettingPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        switchTheme = view.findViewById(R.id.switch_theme)
        switchDailyReminder = view.findViewById(R.id.switch_daily_reminder)

        settingPreferences = SettingPreferences.getInstance(requireContext().dataStore)
        themeViewModel = ViewModelProvider(this, ThemeViewModelFactory(settingPreferences)).get(
            ThemeViewModel::class.java
        )

        lifecycleScope.launch {
            settingPreferences.getThemeSetting().collect { isDarkModeActive ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                switchTheme.isChecked = isDarkModeActive
            }
        }

        lifecycleScope.launch {
            settingPreferences.getReminderSetting().collect { isReminderActive ->
                switchDailyReminder.isChecked = isReminderActive
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked ->
            lifecycleScope.launch {
                settingPreferences.saveThemeSetting(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        switchDailyReminder.setOnCheckedChangeListener { _: CompoundButton?, isChecked ->
            lifecycleScope.launch {
                settingPreferences.saveReminderSetting(isChecked)
                if (isChecked) {
                    startDailyReminder()
                } else {
                    stopDailyReminder()
                }
            }
        }

        return view
    }

    private fun startDailyReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(requireContext()).enqueue(reminderRequest)
    }

    private fun stopDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(ReminderWorker.WORK_NAME)
    }
}
