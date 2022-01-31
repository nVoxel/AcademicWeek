package com.voxeldev.academicweek.ui.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.voxeldev.academicweek.R
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class AppWidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private var onClickListener = MaterialPickerOnPositiveButtonClickListener<Long> {
        val context = this@AppWidgetConfigureActivity

        saveDatePref(context, appWidgetId, TimeUnit.MILLISECONDS.toSeconds(it))

        val appWidgetManager = AppWidgetManager.getInstance(context)
        updateAppWidget(context, appWidgetManager, appWidgetId)

        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(RESULT_CANCELED)

        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_count_date)
            .setSelection(
                TimeUnit.SECONDS.toMillis(
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                )
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener(onClickListener)
                addOnNegativeButtonClickListener { finish() }
                addOnCancelListener { finish() }
                showNow(supportFragmentManager, "widgetDatePicker")
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
    }
}

private const val PREFS_NAME = "com.voxeldev.academicweek.AppWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

internal fun saveDatePref(context: Context, appWidgetId: Int, date : Long) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putLong(PREF_PREFIX_KEY + appWidgetId, date)
    prefs.apply()
}

internal fun loadDatePref(context: Context, appWidgetId: Int): Long {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, -1L)
}

internal fun deleteDatePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}