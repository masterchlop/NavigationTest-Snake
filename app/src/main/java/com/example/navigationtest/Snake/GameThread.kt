package com.example.navigationtest.Snake

import android.util.Log

class GameThread(game: Fragment_snake_game) {
    private val gameObject: Fragment_snake_game = game

    fun run() {
        var bool = true
        var startTime : Long
        var time : Long
        val targetTime = (1000 / TARGET_FPS).toLong()

        while (bool) {
            startTime = System.nanoTime()
            bool = gameObject.increment()
            time = targetTime - (System.nanoTime() - startTime) / 1000000
            if (time >= 0) {
                Thread.sleep(time)
            }
            else {
                Log.i("frameTime", "took $time to long to create frame")
            }
        }
    }

    companion object {
        const val TARGET_FPS = 5
    }
}