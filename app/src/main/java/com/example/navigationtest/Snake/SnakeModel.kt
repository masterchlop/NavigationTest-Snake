package com.example.navigationtest.Snake

import android.util.Log
import kotlin.math.abs



data class SnakeModel(val snakeIndex: Int) {
    private var alive: Boolean = true
    private var direction: Int = when (snakeIndex) {
        0 -> Fragment_snake_game.RIGHT
        1 -> Fragment_snake_game.LEFT
        2 -> Fragment_snake_game.BOTTOM
        3 -> Fragment_snake_game.TOP
        else -> -1
    }

    private var snakePositions: MutableList<Pair<Int, Int>> = mutableListOf()
    //only used by snakes controlled by ai, used to get over obstacles
    private var altAlgorithm: Int = 0
    private var altDirection: Int = -1

    init {
        for (i in 0 until BASE_SNAKE_LEN) {
            when (snakeIndex) {
                0 -> snakePositions.add(Pair(BASE_SNAKE_LEN + 1 - i, Fragment_snake_game.MAP_HEIGHT / 2))
                1 -> snakePositions.add(Pair(Fragment_snake_game.MAP_WIDTH - (BASE_SNAKE_LEN + 1 - i), Fragment_snake_game.MAP_HEIGHT / 2))
                2 -> snakePositions.add(Pair(Fragment_snake_game.MAP_WIDTH / 2, BASE_SNAKE_LEN + 1 - i))
                3 -> snakePositions.add(Pair(Fragment_snake_game.MAP_WIDTH / 2, Fragment_snake_game.MAP_HEIGHT - (BASE_SNAKE_LEN + 1 - i)))
            }
        }
        Log.i("snakeCreated", snakePositions.size.toString())
    }

    fun getSnakeCoordinates(): MutableList<Pair<Int, Int>> {
        return snakePositions
    }

    fun getSnakeCoordinatesNoHead(): MutableList<Pair<Int, Int>> {
        val tmp = mutableListOf<Pair<Int, Int>>()

        for (i in 1 until snakePositions.size)
        {
            tmp.add(snakePositions[i])
        }
        return tmp
    }

    fun getDirection(): Int {
        return direction
    }

    fun setDirection(direction: Int) {
        if (direction in 0..3 && abs(this.direction - direction) != 2)
        {
            this.direction = direction
        }
    }

    fun move(): Boolean {
        when (direction) {
            Fragment_snake_game.LEFT -> {
                if (head().first == 0)
                    return false
                snakePositions.add(0, Pair(head().first - 1, head().second))
            }
            Fragment_snake_game.RIGHT -> {
                if (head().first == Fragment_snake_game.MAP_WIDTH - 1)
                    return false
                snakePositions.add(0, Pair(head().first + 1, head().second))
            }
            Fragment_snake_game.BOTTOM -> {
                if (head().second == 0)
                    return false
                snakePositions.add(0, Pair(head().first, head().second - 1))
            }
            Fragment_snake_game.TOP -> {
                if (head().second == Fragment_snake_game.MAP_HEIGHT - 1)
                    return false
                snakePositions.add(0, Pair(head().first, head().second + 1))
            }
        }
        if (!Fragment_snake_game.apple(head().first, head().second)) {
            snakePositions.removeAt(snakePositions.size - 1)
        }
        else {
            if (snakeIndex == 0) {
                Fragment_snake_game.addPlayersPoint()
            }
            Fragment_snake_game.spawnApple()
        }
        return true
    }

    fun head(): Pair<Int, Int> {
        return snakePositions[0]
    }

    fun isAlive(): Boolean {
        return alive
    }

    fun died() {
        Log.i("snakeDead", "snakeDied: $snakeIndex, head: ${head()}")
        alive = false
    }

    fun getAltAlgorithm(): Int {
        return altAlgorithm
    }

    fun getAltDirection(): Int {
        return altDirection
    }


    fun setAltAlgorithm(iterations: Int) {
        altAlgorithm = iterations
        altDirection = when {
            head().first < Fragment_snake_game.MAP_WIDTH / 3 -> Fragment_snake_game.RIGHT
            head().first > 2 * Fragment_snake_game.MAP_WIDTH / 3 -> Fragment_snake_game.LEFT
            head().second < Fragment_snake_game.MAP_HEIGHT / 3 -> Fragment_snake_game.BOTTOM
            else -> Fragment_snake_game.TOP
        }
    }

    companion object {
        const val BASE_SNAKE_LEN = 3
    }
}
