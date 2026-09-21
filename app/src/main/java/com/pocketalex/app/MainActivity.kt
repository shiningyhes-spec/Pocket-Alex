package com.pocketalex.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Pocket Alex 🐺\n\n把我加到桌面吧。♡\n\n第一版：時間狀態、戳狼台詞、點 ChatGPT。"
            textSize = 22f
            setPadding(48, 80, 48, 48)
        })
    }
}
