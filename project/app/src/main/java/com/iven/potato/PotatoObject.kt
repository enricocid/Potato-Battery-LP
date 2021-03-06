package com.iven.potato

import android.content.Context
import android.graphics.*
import androidx.annotation.NonNull

object PotatoObject {

    fun draw(

        @NonNull context: Context,
        @NonNull c: Canvas?,
        @NonNull potatoPaint: Paint,
        @NonNull potatoStrokePaint: Paint,
        @NonNull potatoMatrix: Matrix,
        @NonNull potatoPath: Path,
        cw: Float,
        ch: Float,
        batLevel: Int
    ) {
        val ph = cw * 0.75F
        val ow = 200f
        val oh = 200f

        val od = if (cw / ow < ph / oh) cw / ow else ph / oh

        c?.translate((cw - od * ow) / 2f, (ch - od * oh) / 2f)

        potatoMatrix.reset()
        potatoMatrix.setScale(od, od)
        c?.scale(1.78f, 1.78f)

        if (PotatoPreferences.isGradientEnabled(context)) {
            //fill the potato with a gradient dependent by the battery level (when
            //approaching to low level the gradients gradually disappears)
            potatoPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                ch,
                Color.BLACK,
                Color.argb(batLevel * 255 / 100, 150, 150, 150),
                Shader.TileMode.CLAMP
            )
        }

        //get stroke color according to the battery level
        potatoStrokePaint.color = getBatteryColor(context, batLevel)

        potatoPath.reset()
        potatoPath.moveTo(56.32f, 0.0f)
        potatoPath.moveTo(86.22f, 82.0f)
        potatoPath.cubicTo(81.22f, 90.49f, 70.84f, 93.56f, 63.12f, 92.69f)
        potatoPath.cubicTo(52.07f, 91.43f, 45.0f, 81.86f, 37.83f, 72.06f)
        potatoPath.cubicTo(26.0f, 50.77f, 23.0f, 44.62f, 23.83f, 36.89f)
        potatoPath.cubicTo(24.1f, 34.05f, 24.64f, 28.73f, 28.91f, 24.57f)
        potatoPath.cubicTo(32.5f, 21.06f, 38.33f, 18.83f, 43.42f, 20.26f)
        potatoPath.cubicTo(47.25f, 21.33f, 47.83f, 23.66f, 54.71f, 31.09f)
        potatoPath.cubicTo(65.66f, 41.83f, 67.59f, 43.31f, 68.64f, 44.13f)
        potatoPath.cubicTo(70.57f, 45.66f, 71.53f, 46.42f, 72.8f, 47.25f)
        potatoPath.cubicTo(76.28f, 49.57f, 78.27f, 50.25f, 81.01f, 52.76f)
        potatoPath.cubicTo(87.7f, 60.81f, 91.82f, 72.43f, 86.22f, 82.0f)

        potatoPath.transform(potatoMatrix)

        c?.drawPath(potatoPath, potatoPaint)
        c?.drawPath(potatoPath, potatoStrokePaint)
    }

    private fun getBatteryColor(@NonNull context: Context, batLevel: Int): Int {

        return when (batLevel) {
            in 93..100 -> context.getColor(R.color.green_01)
            in 85..92 -> context.getColor(R.color.green_02)
            in 77..84 -> context.getColor(R.color.green_03)
            in 69..76 -> context.getColor(R.color.green_04)
            in 61..68 -> context.getColor(R.color.yellow_01)
            in 53..60 -> context.getColor(R.color.yellow_02)
            in 45..52 -> context.getColor(R.color.yellow_03)
            in 37..44 -> context.getColor(R.color.yellow_04)
            in 29..36 -> context.getColor(R.color.orange_01)
            in 21..28 -> context.getColor(R.color.orange_02)
            in 16..20 -> context.getColor(R.color.orange_03)
            in 11..15 -> context.getColor(R.color.orange_04)
            else -> context.getColor(R.color.red)
        }
    }
}