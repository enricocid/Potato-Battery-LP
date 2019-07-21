package com.iven.potato

import android.content.Context
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

    private var mPotatoPaint = Paint()
    private var mPotatoStrokePaint = Paint()
    private var mPotatoPath = Path()
    private var mPotatoMatrix = Matrix()

    private var mDeviceWidth = 0F
    private var mDeviceHeight = 0F

    private var mBatteryLevel = -1

    //the potato battery live potato_wallpaper service and engine
    override fun onCreateEngine(): Engine {

        if (baseContext != null) {
            //retrieve display specifications
            val window = baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val d = DisplayMetrics()
            window.defaultDisplay.getRealMetrics(d)
            mDeviceWidth = d.widthPixels.toFloat()
            mDeviceHeight = d.heightPixels.toFloat()
        }

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
        private val drawRunner = Runnable { draw() }

        override fun onVisibilityChanged(visible: Boolean) {
            sVisible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
                mBatteryLevel = -1
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            sVisible = false
            handler.removeCallbacks(drawRunner)
            mBatteryLevel = -1
        }

        override fun onDestroy() {
            super.onDestroy()
            sVisible = false
            handler.removeCallbacks(drawRunner)
            mBatteryLevel = -1
        }

        //draw potato according to battery level
        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {

                //get battery level
                val batteryManager = baseContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val batLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

                //draw only if battery level has changed
                if (mBatteryLevel != batLevel) {
                    mBatteryLevel = batLevel
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
                            mBatteryLevel
                        )
                    }
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)

            if (sVisible && baseContext != null) handler.postDelayed(
                drawRunner,
                PotatoPreferences.getRefreshTime(baseContext)
            )
        }
    }
}