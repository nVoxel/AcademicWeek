package com.voxeldev.academicweek.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.voxeldev.academicweek.R
import com.voxeldev.academicweek.models.AcademicWeek
import com.voxeldev.academicweek.models.AcademicWeekCalculator

class MainActivity : AppCompatActivity() {

    private var academicWeekCalculator : AcademicWeekCalculator? = null
    private var weekConstraintLayout : ConstraintLayout? = null
    private var weekTextView : MaterialTextView? = null
    private var countingTextView : MaterialTextView? = null
    private var errorTextView : MaterialTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val topAppBar = findViewById<MaterialToolbar>(R.id.mainAppBar)
        topAppBar.setOnMenuItemClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            true
        }

        academicWeekCalculator = AcademicWeekCalculator(applicationContext)

        weekConstraintLayout = findViewById(R.id.weekConstraintLayout)
        weekTextView = findViewById(R.id.weekTextView)
        countingTextView = findViewById(R.id.countingTextView)
        errorTextView = findViewById(R.id.errorTextView)
    }

    override fun onStart() {
        super.onStart()

        val academicWeek: AcademicWeek?

        try {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(applicationContext)
            val countStart : Long = sharedPreferences.getLong("countStart", -1L)
            academicWeek = academicWeekCalculator?.calculateWeek(countStart)
        }
        catch (e : Exception) {
            errorTextView?.setText(e.message)
            toggleError(true)
            return
        }

        weekTextView?.text = academicWeek?.week?.toString()
        countingTextView?.text = academicWeek?.countingFrom
        toggleError(false)
    }

    companion object {
        fun setTheme(theme : String) {
            when (theme) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun toggleError(error: Boolean) {
        weekConstraintLayout?.visibility = if (error) GONE else VISIBLE
        errorTextView?.visibility = if (error) VISIBLE else GONE
    }
}