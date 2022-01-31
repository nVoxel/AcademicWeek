package com.voxeldev.academicweek.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.voxeldev.academicweek.R
import com.voxeldev.academicweek.models.AcademicWeekCalculator
import com.voxeldev.academicweek.ui.MainActivity
import java.lang.Exception

class AppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            deleteDatePref(context, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.app_widget).apply {
        setOnClickPendingIntent(R.id.widgetLinearLayout, PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        ))
    }

    try {
        views.setTextViewText(R.id.widgetWeekTextView,
            AcademicWeekCalculator(context).calculateWeek(
                loadDatePref(context, appWidgetId)).week.toString())
        toggleError(views, false)
    }
    catch (ignored : Exception) {
        toggleError(views, true)
    }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun toggleError(views: RemoteViews, isError : Boolean) {
    views.setViewVisibility(R.id.widgetLinearLayout, if (isError) View.GONE else View.VISIBLE)
    views.setViewVisibility(R.id.widgetErrorTextView, if (isError) View.VISIBLE else View.GONE)
}