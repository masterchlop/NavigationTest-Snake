package com.example.navigationtest.Snake

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.navigationtest.Data.AppDatabase
import com.example.navigationtest.Data.LeaderBoards
import com.example.navigationtest.R
import com.example.navigationtest.databinding.FragmentSnakeBinding
import com.example.navigationtest.rankings.DeviceRankFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.abs
import kotlin.random.Random

class Fragment_snake_game : Fragment() {
    val args :Fragment_snake_gameArgs by navArgs()
    private lateinit var binding: FragmentSnakeBinding
    private var PlayerName=""
    private var mapLegend = HashMap<Byte?, Int>()
    private var screenWidth = 9
    private var screenHeight = 15
    private var playerPosX = -1
    private var playerPosY = -1
    private lateinit var compass: AppleCompass
    private lateinit var database:AppDatabase

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentSnakeBinding.inflate(inflater,container,false)
        val view = binding.root
        PlayerName = args.playerName
        Log.d("essa",PlayerName)
        Log.d("essa2",args.playerName)
        map = MapGenerator(MAP_WIDTH, MAP_HEIGHT).getMap()
        GlobalScope.launch{
            try {
                database = Room.databaseBuilder(
                    requireContext(),
                    AppDatabase::class.java,
                    "ranking3.db"
                ).build()
            } catch (e: Exception) {
                Log.d("am2021", e.message.toString())
            }

        }

        snakes = mutableListOf()
        for (i in 0 .. enemySnakeCount) {
            snakes.add(SnakeModel(i))
        }

        scoreText = binding.scoreTextView
        playerPosX = snakes[0].head().first
        playerPosY = snakes[0].head().second



        mapLegend[0.toByte()] = R.drawable.empty
        mapLegend[1.toByte()] = R.drawable.map_field

        spawnApple()
        compass = binding.appleCompass
        colorImages()

        binding.frameLayout.setOnTouchListener (object: OnSwipeListener(requireContext()) {
            override fun onSwipeLeft() {
                snakes[0].setDirection(LEFT)
            }
            override fun onSwipeRight() {
                snakes[0].setDirection(RIGHT)
            }
            override fun onSwipeTop() {
                snakes[0].setDirection(TOP)
            }
            override fun onSwipeBottom() {
                snakes[0].setDirection(BOTTOM)
            }
        })
        val threadTwo = GameThread(this)
        GlobalScope.launch {threadTwo.run()}

        return view
    }

    private fun colorImages() {
        for (tableRow: View in binding.tableLayout) {
            if (tableRow is TableRow) {
                for (img: View in tableRow) {
                    if (img is ImageView) {
                        val id: Int = Integer.parseInt(img.tag.toString())
                        //translating imageView id to map coordinates
                        val xOffset = playerPosX + id % screenWidth - screenWidth / 2
                        val yOffset = playerPosY + screenHeight - id / screenWidth - 1 - screenHeight / 2

                        val biomeIndex = if (xOffset < 0 || xOffset >= MAP_WIDTH || yOffset >= MAP_HEIGHT || yOffset < 0) {
                            0
                        }
                        else {
                            (map[yOffset][xOffset] % 32).toByte()
                        }

                        //draw snakes
                        when (whichSnakeOnField(xOffset, yOffset)) {
                            0 -> {
                                img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.green_blob))
                            }
                            1 -> {
                                img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.red_blob))
                            }
                            2 -> {
                                img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.blue_blob))
                            }
                            3 -> {
                                img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.yellow_blob))
                            }
                            else -> {
                                img.setImageDrawable(null)
                            }
                        }

                        //draw apple
                        if (xOffset == appleCoordinates.first && yOffset == appleCoordinates.second)
                        {
                            img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.apple))
                        }

                        //draw map fields
                        if (biomeIndex >= 1) {
                            img.setBackgroundResource(mapLegend[biomeIndex]!!)
                        }
                        else {
                            img.setBackgroundResource(R.drawable.empty)
                            img.setImageDrawable(null)
                        }
                    }
                }
            }
        }
    }








    private fun whichSnakeOnField(X: Int, Y: Int): Int {
        for (i in 0 .. enemySnakeCount) {
            if (snakes[i].isAlive() && Pair(X, Y) in snakes[i].getSnakeCoordinates())
                return i
        }
        return -1
    }

    fun increment(): Boolean {
        //enemmies choose direction for the next move
        for (i in 1 .. enemySnakeCount) {
            if (snakes[i].isAlive()) {
                chooseMoveDirection(snakes[i])
            }
        }

        //all snakes move in their chosen direction and die if they go out of the map
        for (i in 0 .. enemySnakeCount) {
            if (snakes[i].isAlive()) {
                if (!snakes[i].move()) {
                    Log.i("snakeDead", "$i !move")
                    snakes[i].died()
                }
            }
        }
        //check if any snake died after their move (hit an obstacle / snake)
        for (i in 0 .. enemySnakeCount) {
            if (snakes[i].isAlive()) {
                if (!checkMoveLegal(i)) {
                    snakes[i].died()
                }
            }
        }
        if (!snakes[0].isAlive())
        {
            playerDied()
            return false
        }
        playerPosX = snakes[0].head().first
        playerPosY = snakes[0].head().second
        compass.newFrame(playerPosX, playerPosY, appleCoordinates)
        CoroutineScope(Dispatchers.Main).launch {colorImages()}
        return true
    }


    private fun chooseMoveDirection(snake: SnakeModel) {
        // alternatywny algorytm, ktoryt wlacza sie gdy waz zaczyna sie krecic w kolko, bo jablko jest za przeszkoda
        // istnieje pewne prawdopodobientswo, ze waz przejdzie za przeszkode korzystajac z tego prymitywnego ulepszenia,
        // a nawet jesli mu sie nie uda, to przynajmniej bedzie wygladal mniej glupio
        // jesli alternatywny algorytm wpadl by w przeszkode, to przez jedna iteracje skorzystamy z normalnego, ale nie zmniejszymy licznika
        if (snake.getAltAlgorithm() != 0)
        {
            val tmp = coordinatesAfterMove(snake, snake.getAltDirection())
            if (tmp.second in 0 until MAP_HEIGHT && tmp.first in 0 until MAP_WIDTH && fieldEmpty(tmp.first, tmp.second))
            {
                snake.setAltAlgorithm(snake.getAltAlgorithm() - 1)
                snake.setDirection(snake.getAltDirection())
                return
            }
        }

        if (snake.head().first == appleCoordinates.first || snake.head().second == appleCoordinates.second) {
            var tmp = coordinatesAfterMove(snake, snake.getDirection())
            if (tmp.second in 0 until MAP_HEIGHT && tmp.first in 0 until MAP_WIDTH && map[tmp.second][tmp.first] == 0.toByte()) {
                val direction = snake.getDirection()
                var counter = 0

                //mierzenie szerokości przeszkody, powinna być podobna do wysokości
                while (tmp.second in 0 until MAP_HEIGHT && tmp.first in 0 until MAP_WIDTH && map[tmp.second][tmp.first] == 0.toByte())
                {
                    tmp = coordinatesAfterMove(tmp, direction)
                    counter++
                }

                if (tmp.second in 0 until MAP_HEIGHT && tmp.first in 0 until MAP_WIDTH)
                {
                    snake.setAltAlgorithm(counter)
                }
            }
        }

        //podstawowy algorytm, gdy alternatywny nie zostal zastosowany
        val possibleDirections = when(val currentDirection = snake.getDirection()) {
            0 -> mutableListOf(3, 0, 1)
            3 -> mutableListOf(2, 3, 0)
            else -> mutableListOf(currentDirection - 1, currentDirection, currentDirection + 1)
        }
        val possibleCoordinates: MutableList<Pair<Int, Int>> = mutableListOf()
        //manhattan distance of a point to the apple
        val coordinatesHeuristic: MutableList<Int> = mutableListOf()

        for (direction in possibleDirections) {
            possibleCoordinates.add(coordinatesAfterMove(snake, direction))
        }
        val iterator = possibleCoordinates.iterator()
        while (iterator.hasNext()) {
            val coordinates = iterator.next()
            if (coordinates.first < 0 || coordinates.second < 0 || coordinates.first >= MAP_WIDTH || coordinates.second >= MAP_HEIGHT)
            {
                possibleDirections.removeAt(possibleCoordinates.indexOf(coordinates))
                iterator.remove()
            }
            else if (!fieldEmpty(coordinates.first, coordinates.second))
            {
                possibleDirections.removeAt(possibleCoordinates.indexOf(coordinates))
                iterator.remove()
            }
            else
            {
                coordinatesHeuristic.add(abs(coordinates.first - appleCoordinates.first) + abs(coordinates.second - appleCoordinates.second))
            }
        }
        var minHeuristic = MAP_HEIGHT + MAP_WIDTH
        var minHeuristicIndex = -1

        for (heuristic in coordinatesHeuristic) {
            if (heuristic < minHeuristic) {
                minHeuristic = heuristic
                minHeuristicIndex = coordinatesHeuristic.indexOf(heuristic)
            }
            if (heuristic == minHeuristic) {
                //if two directions give the same distance to the apple choose a random one
                if (Random.nextDouble() >= 0.5) {
                    minHeuristic = heuristic
                    minHeuristicIndex = coordinatesHeuristic.indexOf(heuristic)
                }
            }
        }

        if (minHeuristicIndex != -1)
            snake.setDirection(possibleDirections[minHeuristicIndex])
    }

    private fun coordinatesAfterMove(snake: SnakeModel, direction: Int): Pair<Int, Int> {
        return when(direction) {
            LEFT -> Pair(snake.head().first - 1, snake.head().second)
            RIGHT -> Pair(snake.head().first + 1, snake.head().second)
            TOP -> Pair(snake.head().first, snake.head().second + 1)
            BOTTOM -> Pair(snake.head().first, snake.head().second - 1)
            else -> Pair(-1, -1)
        }
    }

    private fun coordinatesAfterMove(coordinates: Pair<Int, Int>, direction: Int): Pair<Int, Int> {
        return when(direction) {
            LEFT -> Pair(coordinates.first - 1, coordinates.second)
            RIGHT -> Pair(coordinates.first + 1, coordinates.second)
            TOP -> Pair(coordinates.first, coordinates.second + 1)
            BOTTOM -> Pair(coordinates.first, coordinates.second - 1)
            else -> Pair(-1, -1)
        }
    }

    private fun checkMoveLegal(i: Int): Boolean {
        //check if snake is out of map
        if (map[snakes[i].head().second][snakes[i].head().first] == 0.toByte()) {
            Log.i("snakeDead", "$i !moveLegal out of the map, direction: ${snakes[i].getDirection()}")
            return false
        }
        for (j in 0 .. enemySnakeCount) {
            //check if snake hit its own tail
            if (i == j) {
                if (snakes[i].head() in snakes[i].getSnakeCoordinatesNoHead()) {
                    Log.i("snakeDead", "$i !moveLegal, hit its own tail, direction: ${snakes[i].getDirection()}")
                    return false
                }
            }
            //check if snake hit another snake
            else if (snakes[j].isAlive() && snakes[i].head() in snakes[j].getSnakeCoordinates()) {
                Log.i("snakeDead", "$i !moveLegal, hit another snake, direction: ${snakes[i].getDirection()}")
                return false
            }
        }
        return true
    }

    private fun playerDied(){

        val Score = LeaderBoards(PlayerName, playerPoints)
        database.LeaderBoardsDAO().insertRanking(Score)
        requireActivity().runOnUiThread {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Game over!")
            builder.setMessage("You have collected $playerPoints apples!")
            builder.setNegativeButton("Back to menu") { _, _ ->
                playerPoints = 0
                Navigation.findNavController(requireView()).navigate(R.id.action_fragment_snake_to_fragment_one)
            }
            builder.setPositiveButton("Try again"){_,_ ->
                playerPoints = 0
                val action = Fragment_snake_gameDirections.actionFragmentSnakeSelf(PlayerName)
                Navigation.findNavController(requireView()).navigate(action)
            }
            val alert = builder.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }
    }

    companion object {
        lateinit var scoreText: TextView

        const val MAP_WIDTH = 30
        const val MAP_HEIGHT = 25
        const val enemySnakeCount = 3

        const val BOTTOM = 0
        const val LEFT = 1
        const val TOP = 2
        const val RIGHT = 3


        private var appleCoordinates: Pair<Int, Int> = Pair(-1, -1)
        private lateinit var map: Array<ByteArray>
        private var snakes: MutableList<SnakeModel> = mutableListOf()
        private var playerPoints = 0

        fun apple(x: Int, y: Int): Boolean {
            return Pair(x, y) == appleCoordinates
        }

        fun spawnApple() {
            var r1: Int
            var r2: Int

            do {
                r1 = Random.nextInt(MAP_WIDTH)
                r2 = Random.nextInt(MAP_HEIGHT)
            } while (!fieldEmpty(r1, r2))

            appleCoordinates = Pair(r1, r2)

            for (i in 1 .. enemySnakeCount) {
                //po stworzeniu nowego jablka, przestan gonic stare
                snakes[i].setAltAlgorithm(0)
            }
        }

        private fun fieldEmpty(x: Int, y: Int): Boolean {
            if (map[y][x] == 0.toByte())
                return false
            for (i in 0 .. enemySnakeCount) {
                if (snakes[i].isAlive() && Pair(x, y) in snakes[i].getSnakeCoordinates())
                {
                    return false
                }
            }
            return true
        }

        fun addPlayersPoint() {
            playerPoints++
            scoreText.text = "Score: $playerPoints"
        }

        fun getScore(): Int {
            return playerPoints
        }
    }
}