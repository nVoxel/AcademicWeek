package com.voxeldev.academicweek.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.voxeldev.academicweek.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class AppWidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var isDateSelected = false
    private var displayTotalWeeks = false

    private var onClickListener =
        MaterialPickerOnPositiveButtonClickListener<androidx.core.util.Pair<Long, Long>> {
            val context = this@AppWidgetConfigureActivity

            saveDatePref(
                context,
                appWidgetId,
                TimeUnit.MILLISECONDS.toSeconds(it.first) to
                        TimeUnit.MILLISECONDS.toSeconds(it.second)
            )

            isDateSelected = true

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
        }

    private val materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
        .setTitleText(R.string.select_count_date)
        .setSelection(
            androidx.core.util.Pair(
                TimeUnit.SECONDS.toMillis(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                ),
                TimeUnit.SECONDS.toMillis(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                )
            )
        )
        .build()
        .apply {
            addOnPositiveButtonClickListener(onClickListener)
            addOnNegativeButtonClickListener { finish() }
            addOnCancelListener { finish() }
        }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setContentView(R.layout.activity_app_widget_configure)

        setResult(RESULT_CANCELED)

        findViewById<MaterialButton>(R.id.setCountDateButton).setOnClickListener {
            Toast.makeText(
                applicationContext,
                R.string.selection_hint,
                Toast.LENGTH_LONG
            ).show()

            materialDatePicker.showNow(supportFragmentManager, "widgetDatePicker")
        }

        findViewById<MaterialCheckBox>(R.id.displayTotalWeeksCheckBox)
            .setOnCheckedChangeListener { _, isChecked ->
                displayTotalWeeks = isChecked
                saveTotalWeeksPref(this, appWidgetId, displayTotalWeeks)
            }

        findViewById<MaterialButton>(R.id.continueButton).setOnClickListener {
            if (isDateSelected) {
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                updateAppWidget(applicationContext, appWidgetManager, appWidgetId)

                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.select_count_dates_error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        if (icicle != null) {
            isDateSelected = icicle.getBoolean(KEY_IS_DATE_SELECTED, false)

            if (isDateSelected) {
                val resultValue = Intent()
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                setResult(RESULT_OK, resultValue)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_DATE_SELECTED, isDateSelected)
        super.onSaveInstanceState(outState)
    }
}

private const val PREFS_NAME = "com.voxeldev.academicweek.AppWidget"
private const val PREF_START_PREFIX_KEY = "appwidget_start_"
private const val PREF_END_PREFIX_KEY = "appwidget_end_"
private const val PREF_TOTAL_WEEKS_PREFIX_KEY = "appwidget_total_weeks"
private const val KEY_IS_DATE_SELECTED = "isDateSelected"

internal fun saveDatePref(context: Context, appWidgetId: Int, date: Pair<Long, Long>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putLong(PREF_START_PREFIX_KEY + appWidgetId, date.first)
    prefs.putLong(PREF_END_PREFIX_KEY + appWidgetId, date.second)
    prefs.apply()
}

internal fun loadDatePref(context: Context, appWidgetId: Int): Pair<Long, Long> {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getLong(PREF_START_PREFIX_KEY + appWidgetId, -1L) to
            prefs.getLong(PREF_END_PREFIX_KEY + appWidgetId, -1L)
}

internal fun deleteDatePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_START_PREFIX_KEY + appWidgetId)
    prefs.remove(PREF_END_PREFIX_KEY + appWidgetId)
    prefs.apply()
}

internal fun saveTotalWeeksPref(context: Context, appWidgetId: Int, displayTotalWeeks: Boolean) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putBoolean(PREF_TOTAL_WEEKS_PREFIX_KEY + appWidgetId, displayTotalWeeks)
    prefs.apply()
}

internal fun loadTotalWeeksPref(context: Context, appWidgetId: Int): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getBoolean(PREF_TOTAL_WEEKS_PREFIX_KEY + appWidgetId, false)
}

internal fun deleteTotalWeeksPref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_TOTAL_WEEKS_PREFIX_KEY + appWidgetId)
    prefs.apply()
}