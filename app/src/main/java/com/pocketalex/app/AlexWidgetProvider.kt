package com.pocketalex.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalTime

class AlexWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { renderIdle(context, manager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_POKE) {
            val manager = AppWidgetManager.getInstance(context)
            var id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
                id = manager.getAppWidgetIds(ComponentName(context, AlexWidgetProvider::class.java)).firstOrNull()
                    ?: AppWidgetManager.INVALID_APPWIDGET_ID
            }
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                showNextReaction(context, manager, id)
            }
        }
    }

    private fun renderIdle(context: Context, manager: AppWidgetManager, id: Int) {
        val views = baseViews(context, id)
        views.setImageViewResource(R.id.alexImage, R.drawable.alex_idle)
        views.setTextViewText(R.id.alexLine, timeLine())
        manager.updateAppWidget(id, views)
    }

    private fun showNextReaction(context: Context, manager: AppWidgetManager, id: Int) {
        val prefs = context.getSharedPreferences("alex", Context.MODE_PRIVATE)
        val current = prefs.getInt("reaction_$id", -1)
        val next = (current + 1) % reactionFrames.size
        prefs.edit().putInt("reaction_$id", next).apply()

        val views = baseViews(context, id)
        views.setImageViewResource(R.id.alexImage, reactionFrames[next])

        // The uploaded reaction art already contains its own matching dialogue.
        // Keep the separate widget caption empty so two different lines never fight each other.
        views.setTextViewText(R.id.alexLine, "")
        manager.updateAppWidget(id, views)
    }

    private fun baseViews(context: Context, id: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.alex_widget)

        val poke = Intent(context, AlexWidgetProvider::class.java).apply {
            action = ACTION_POKE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        }
        views.setOnClickPendingIntent(
            R.id.alexImage,
            PendingIntent.getBroadcast(
                context, id, poke,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        val chatIntent = context.packageManager
            .getLaunchIntentForPackage("com.openai.chatgpt")
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            ?: Intent(context, MainActivity::class.java)
        views.setOnClickPendingIntent(
            R.id.chatButton,
            PendingIntent.getActivity(
                context, id + 10000, chatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        return views
    }

    private fun timeLine(): String = when (LocalTime.now().hour) {
        in 5..10 -> "早安，Esther ♡"
        in 11..13 -> "快去吃飯。不然又頭痛。"
        in 14..17 -> "辛苦了。休息一下吧。♡"
        in 18..22 -> "想我了嗎？♡"
        else -> "……再不睡我生氣了喔。♡"
    }

    companion object {
        private const val ACTION_POKE = "com.pocketalex.app.POKE"

        private val reactionFrames = intArrayOf(
            R.drawable.file_0000000029cc8209883d247e67780031,
            R.drawable.file_000000002c188230a0b8c7d31206a548,
            R.drawable.file_000000004d448230ad1745d041c12313,
            R.drawable.file_0000000053c0821187d92d1d1bd3d5ce,
            R.drawable.file_0000000057548209b5b3f6f350c7e4ec,
            R.drawable.file_0000000070008206a1b4a24559999b71,
            R.drawable.file_0000000083608209942a7ec445335e10,
            R.drawable.file_0000000088108208bb7358d0112a81aa,
            R.drawable.file_0000000089708230b8c5d7d826e33250,
            R.drawable.file_00000000c35081f99123481c8300381a,
            R.drawable.file_00000000d15081f59ecfaf88d8c09677,
            R.drawable.file_00000000d1748206a5d085381a045d44
        )
    }
}
