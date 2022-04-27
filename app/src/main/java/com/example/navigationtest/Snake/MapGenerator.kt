package com.example.navigationtest.Snake

import android.util.Log
import kotlin.math.abs
import kotlin.random.Random

class MapGenerator(private val width: Int, private val height: Int) {

    private var map: Array<ByteArray> = Array(height) { ByteArray(width) {1.toByte()} }
    private val edgeRemovalChance: Double = 0.7
    private val minDim: Int = when {
        width < height -> width
        else -> height
    }
    private val obstacleCount: Int = minDim / 5

    fun getMap(): Array<ByteArray> {
        generateMap()
        printMap()
        return map
    }


    private fun generateMap() {
        cutCorners()

        for (i in 0 until obstacleCount) {
            val r = Random.nextInt(minDim / 10, minDim / 5)
            val x = Random.nextInt((width * 0.05).toInt(), (width * 0.95).toInt()).toByte()
            val y = Random.nextInt((height * 0.05).toInt(), (height * 0.95).toInt()).toByte()
            deleteByDist(x, y, r)
        }

        repairSpawnPoints()
        polishEdges()
    }


    //powoduje, że mapa ma obcięte rogi, mniej więcej według owalu na środku mapy
    private fun cutCorners() {
        for (i in 0 until width)  {
            for (j in 0 until height) {
                val xDist = abs(i - width / 2)
                val yDist = abs(j - height / 2)

                if (xDist * xDist + yDist * yDist - abs(xDist - yDist) > (minDim * 0.6) * (minDim * 0.6)) {
                    map[j][i] = 0
                }
            }
        }
    }

    private fun deleteByDist(x: Byte, y: Byte, dist: Int) {
        //troche za mocny warunek na to by "graf mapy był spójny", ale daje też lepszy efekty wizualne mapy
        //tzn zapewnia, że przeszkody nie nakładają się na siebie
        for (i in x - dist - 1 until x + dist + 1) {
            for (j in y - dist - 1 until y + dist + 1) {
                if (i in 0 until width && j in 0 until height) {
                    if (map[j][i] == 0.toByte()) {
                        return deleteByDist(
                            Random.nextInt((width * 0.05).toInt(), (width * 0.95).toInt()).toByte(),
                            Random.nextInt((height * 0.05).toInt(), (height * 0.95).toInt()).toByte(),
                            Random.nextInt(minDim / 10, minDim / 5)
                        )
                    }
                }
            }
        }

        //ustawienie pol które są bliżej niż dist od (x, y) na 0
        for (i in x - dist until x + dist) {
            for (j in y - dist until y + dist) {
                if (i in 0 until width && j in 0 until height) {
                    val xDist = abs(i - x)
                    val yDist = abs(j - y)
                    if (xDist * xDist + yDist * yDist - abs(xDist - yDist) < dist * dist) {
                        map[j][i] = 0
                    }
                }
            }
        }
    }

    //sprawia że przeskody mają bardziej nieregularny kształt
    private fun polishEdges() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (i in 0 until width && j in 0 until height) {
                    if (map[j][i] == 0.toByte()) {
                        var neighboringOnes = 0
                        if (i < width - 1 && map[j][i + 1] >= 1.toByte()) {
                            neighboringOnes++
                        }
                        if (i > 0 && map[j][i - 1] >= 1.toByte()) {
                            neighboringOnes++
                        }
                        if (j > 0 && map[j - 1][i] >= 1.toByte()) {
                            neighboringOnes++
                        }
                        if (j < height - 1 && map[j + 1][i] >= 1.toByte()) {
                            neighboringOnes++
                        }
                        if (neighboringOnes == 2) {
                            if (Random.nextDouble() < edgeRemovalChance) {
                                map[j][i] = 1
                            }
                        }
                    }
                }
            }
        }
    }


    private fun repairSpawnPoints() {
        val middle = Fragment_snake_game.MAP_HEIGHT / 2
        for (i in -1 .. 1) {
            for (j in 0 until Fragment_snake_game.MAP_WIDTH) {
                map[middle + i][j] = 1
            }
        }
        if (Fragment_snake_game.enemySnakeCount > 1)
        {
            val midVert = Fragment_snake_game.MAP_WIDTH / 2
            for (i in -1 .. 1) {
                for (j in 0 until Fragment_snake_game.MAP_HEIGHT) {
                    map[j][midVert + i] = 1
                }
            }
        }
    }

    private fun printMap() {
        for (i in 0 until height) {
            var row = ""
            for (j in 0 until width) {
                row += map[i][j]
                row += " "
            }
            row += " row $i"
            Log.i("mapGen", row)
        }
    }
}