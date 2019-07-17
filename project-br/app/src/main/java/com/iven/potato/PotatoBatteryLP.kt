package com.iven.potato

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.BatteryManager
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import android.view.WindowManager

class PotatoBatteryLP : WallpaperService() {

    private lateinit var mPotatoPaint: Paint
    private lateinit var mPotatoStrokePaint: Paint
    private lateinit var mPotatoPath: Path
    private lateinit var mPotatoMatrix: Matrix

    private var mDeviceWidth: Int = 0
    private var mDeviceHeight: Int = 0

    private lateinit var intentFilter: IntentFilter

    private var sIsBatteryReceiver: Boolean = false

    //the potato battery live potato_wallpaper service and engine
    override fun onCreateEngine(): Engine {

        //instantiate battery changes intent filter
        intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)

        //retrieve display specifications
        val window = baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = DisplayMetrics()
        window.defaultDisplay.getRealMetrics(d)

        mDeviceWidth = d.widthPixels
        mDeviceHeight = d.heightPixels

        //allocate paints, matrix and path
        mPotatoPaint = Paint()
        mPotatoStrokePaint = Paint()
        mPotatoMatrix = Matrix()
        mPotatoPath = Path()

        //set paints props
        mPotatoPaint.isAntiAlias = true
        mPotatoPaint.style = Paint.Style.FILL

        mPotatoStrokePaint.isAntiAlias = true
        mPotatoStrokePaint.style = Paint.Style.STROKE
        mPotatoStrokePaint.strokeWidth = 5F
        return PotatoEngine()
    }

    private inner class PotatoEngine : WallpaperService.Engine() {

        private val handler = Handler()
        private var sVisible = true

        val batteryInfoReceiver = object : BroadcastReceiver() {

            // rest implementation  here
            // or make this an abstract class as template :)
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                handler.post {
                    draw(level)
                }
            }
        }

        private fun addReceiver() {
            if (baseContext != null && !sIsBatteryReceiver) {
                baseContext.registerReceiver(batteryInfoReceiver, intentFilter)
                sIsBatteryReceiver = true
                sVisible = true
            }
        }

        private fun removeReceiver() {
            try {
                if (baseContext != null && sIsBatteryReceiver) {
                    baseContext.unregisterReceiver(batteryInfoReceiver)
                    sIsBatteryReceiver = false
                    sVisible = false
                }

            } catch (e: Exception) {
                sIsBatteryReceiver = false
                sVisible = false
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                addReceiver()
            } else {
                removeReceiver()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            removeReceiver()
        }

        override fun onDestroy() {
            super.onDestroy()
            removeReceiver()
        }

        //draw potato according to battery level
        fun draw(batLevel: Int) {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null && baseContext != null) {

                    PotatoObject.draw(
                        baseContext,
                        canvas,
                        mPotatoPaint,
                        mPotatoStrokePaint,
                        mPotatoMatrix,
                        mPotatoPath,
                        mDeviceWidth,
                        mDeviceHeight,
                        batLevel
                    )
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}