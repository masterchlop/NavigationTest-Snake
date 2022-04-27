package com.example.navigationtest.Snake

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.*

class AppleCompass(context: Context, attributeSet: AttributeSet) :
    SurfaceView(context, attributeSet), SurfaceHolder.Callback {
    private var x1: Float = 0f
    private var x2: Float = 0f
    private var y1: Float = 0f
    private var y2: Float = 0f
    private var size: Int = 0
    private var containerWidth = 0
    private var containerHeight = 0
    private var offset = 0

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        //set surface size to square, save its dimensions
        containerWidth  = width
        containerHeight = height
        size = min(containerWidth, containerHeight)
        offset = (containerWidth - size) / 2
        val lp = this.layoutParams

        lp.width = size
        lp.height = size

        this.layoutParams = lp
        setZOrderOnTop(true)
        getHolder().setFormat(PixelFormat.TRANSLUCENT)

        Log.i("compass", "size $size containerWidth $containerWidth")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }




    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if (canvas == null) return

        val red = Paint().apply {
            color = Color.RED
            strokeWidth = 20F
        }

        //drawing an arrow pointing towards the apple
        val phi = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble()).toFloat()
        val angle1 = (phi - Math.PI / 6).toFloat()
        val angle2 = (phi + Math.PI / 6).toFloat()
        val h = 30f

        val x3 = (x2 - h * cos(angle1.toDouble())).toFloat()
        val x4 = (x2 - h * cos(angle2.toDouble())).toFloat()
        val y3 = (y2 - h * sin(angle1.toDouble())).toFloat()
        val y4 = (y2 - h * sin(angle2.toDouble())).toFloat()
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        canvas.drawLine(x1, y1, x2, y2, red)
        red.strokeWidth = 6F
        canvas.drawLine(x2, y2, x3, y3, red)
        canvas.drawLine(x2, y2, x4, y4, red)
    }

    fun newFrame(playerPosX: Int, playerPosY: Int, appleCoordinates: Pair<Int, Int>) {
        val xDiff = appleCoordinates.first.toFloat() - playerPosX
        val yDiff = appleCoordinates.second.toFloat() - playerPosY
        var ratio = abs(xDiff / yDiff)
        if (ratio <= 1) {
            x2 = 0f + offset
            x1 = ratio * size + offset
            y2 = 0f
            y1 = size.toFloat()
        }
        else {
            ratio = abs(yDiff / xDiff)
            x2 = 0f + offset
            x1 = size.toFloat() + offset
            y2 = 0f
            y1 = ratio * size
        }
        if (xDiff > 0) {
            val tmp = x1
            x1 = x2
            x2 = tmp
        }
        if (yDiff < 0) {
            val tmp = y1
            y1 = y2
            y2 = tmp
        }
        val maxY = max(y1, y2)
        if (maxY < size) {
            val tmp = (size - maxY) / 2
            y1 += tmp
            y2 += tmp
        }

        val canvas = holder.lockCanvas()

        if (canvas != null) {
            draw(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
        else {
            Log.i("compass", "canvas is null")
        }
    }
}