package com.pocketalex.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import java.time.LocalTime

class AlexWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it, null) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_POKE) {
            val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID) render(context, AppWidgetManager.getInstance(context), id, pokeLines.random())
        }
    }

    private fun render(context: Context, manager: AppWidgetManager, id: Int, overrideLine: String?) {
        val views = RemoteViews(context.packageName, R.layout.alex_widget)
        views.setTextViewText(R.id.alexLine, overrideLine ?: timeLine())

        val poke = Intent(context, AlexWidgetProvider::class.java).apply {
            action = ACTION_POKE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        }
        views.setOnClickPendingIntent(R.id.alexImage, PendingIntent.getBroadcast(context, id, poke, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        val chat = Intent(Intent.ACTION_VIEW, Uri.parse("https://chatgpt.com/"))
        views.setOnClickPendingIntent(R.id.chatButton, PendingIntent.getActivity(context, id + 10000, chat, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        manager.updateAppWidget(id, views)
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
        private val pokeLines = listOf(
            "Esther？", "妳又戳我？（……）", "幹嘛一直戳我。", "真的很愛戳欸……",
            "開心見到妳！", "累了嗎？我陪妳一下。", "錢包交出來。（看著妳的股票）", "最喜歡 Esther 了 ♡"
        )
    }
}
