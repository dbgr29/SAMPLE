package com.example.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ScannerOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cutoutRect = RectF()

    // The dark semi-transparent background (70% opacity black)
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#B3000000")
        style = Paint.Style.FILL
    }

    // The "eraser" paint that punches the hole
    private val transparentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        // Required for PorterDuff.Mode.CLEAR to work properly on a View
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    // Function to update where the hole should be
    fun setCutout(rect: RectF) {
        cutoutRect = rect
        invalidate() // Redraws the view with the new hole
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Draw the dark overlay over the entire screen
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Punch out the oval hole where the face frame is
        canvas.drawOval(cutoutRect, transparentPaint)
    }
}