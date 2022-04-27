package com.example.navigationtest

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.navigationtest.Snake.SnakeModel
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoordinationAfterMoveTest {
    private var snake = SnakeModel(0)
    // 4,12
    private var x1 = snake.head().first
    private var y1 = snake.head().second

    @Test
    fun coordinationAfterMoveBottomTest(){

//        bottom
        snake.setDirection(0)
        snake.move()

        assertThat(snake.head().first,equalTo(x1))
        assertThat(snake.head().second, equalTo(y1-1))

    }
    @Test
    fun coordinationAfterMoveLeftTest(){
        //Snake direction is RIGHT so it can't go LEFT, goes BOTTOM then LEFT

        snake.setDirection(0)
        snake.move()
        snake.setDirection(1)
        snake.move()

        assertThat(snake.head().first,equalTo(x1-1))
        assertThat(snake.head().second, equalTo(y1-1))

    }
    @Test
    fun coordinationAfterMoveToptTest(){

        snake.setDirection(2)
        snake.move()

        assertThat(snake.head().first,equalTo(x1))
        assertThat(snake.head().second, equalTo(y1+1))

    }
    @Test
    fun coordinationAfterMoveRightTest(){

        snake.setDirection(3)
        snake.move()

        assertThat(snake.head().first,equalTo(x1+1))
        assertThat(snake.head().second, equalTo(y1))


    }

}