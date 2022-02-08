package com.voxeldev.academicweek.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.voxeldev.academicweek.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsContainer, SettingsFragment())
                .commit()
        }

        findViewById<MaterialToolbar>(R.id.settingsAppBar).setNavigationOnClickListener{
            finish()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)

            val sharedPreferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(requireContext())

            findPreference<Preference>("countStart")?.setOnPreferenceClickListener {
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(R.string.select_count_date)
                    .setSelection(
                        TimeUnit.SECONDS.toMillis(
                            sharedPreferences.getLong(
                                "countStart",
                                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                            )
                        )
                    )
                    .build()
                    .apply {
                        addOnPositiveButtonClickListener {
                            with(sharedPreferences.edit()) {
                                putLong(
                                    "countStart",
                                    TimeUnit.MILLISECONDS.toSeconds(selection!!)
                                )
                                apply()
                                Toast.makeText(
                                    context, R.string.applied_count_date, Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        showNow(this@SettingsFragment.parentFragmentManager, "datePicker")
                    }

                true
            }

            findPreference<ListPreference>("theme")
                ?.setOnPreferenceChangeListener { _, newValue ->
                    if (newValue is String) MainActivity.setTheme(newValue)
                    true
                }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = super.onCreateView(inflater, container, savedInstanceState)
            view.setBackgroundColor(resources.getColor(R.color.background, context?.theme))
            return view
        }
    }
}