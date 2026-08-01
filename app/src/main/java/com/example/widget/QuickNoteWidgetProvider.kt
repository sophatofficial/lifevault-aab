package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class QuickNoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val ACTION_ADD_NOTE = "com.example.ACTION_ADD_NOTE"
        const val ACTION_SCAN_DOC = "com.example.ACTION_SCAN_DOC"
        const val ACTION_OPEN_VAULT = "com.example.ACTION_OPEN_VAULT"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_note)

            // Intent for Header / Container -> Open App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

            // Intent for + New Note button -> Create Note directly
            val addNoteIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_ADD_NOTE
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val addNotePendingIntent = PendingIntent.getActivity(
                context,
                1,
                addNoteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_new_note, addNotePendingIntent)

            // Intent for Scan Doc button
            val scanDocIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_SCAN_DOC
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val scanDocPendingIntent = PendingIntent.getActivity(
                context,
                2,
                scanDocIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_scan, scanDocPendingIntent)

            // Intent for Vault button
            val vaultIntent = Intent(context, MainActivity::class.java).apply {
                action = ACTION_OPEN_VAULT
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val vaultPendingIntent = PendingIntent.getActivity(
                context,
                3,
                vaultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_vault, vaultPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
