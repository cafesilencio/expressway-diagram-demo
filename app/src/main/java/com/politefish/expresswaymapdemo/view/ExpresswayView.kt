package com.politefish.expresswaymapdemo.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageView
import com.mapbox.navigation.utils.internal.ifNonNull
import com.politefish.expresswaymapdemo.domain.model.ExpImagePuckPosition
import kotlin.math.min

@MainThread
class ExpresswayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): AppCompatImageView(context, attrs) {

    init {
        adjustViewBounds = true
        scaleType = ScaleType.FIT_CENTER
    }

    internal var expBitmap: Bitmap? = null
        private set
    private var puckPosition: ExpImagePuckPosition? = null
    private var puckBitmap: Bitmap? = null
    private var puckPaint: Paint? = null
    private var enablePuckRotation = true
    private var puckHalfWidth = 0f
    private var puckHalfHeight = 0f
    private val puckDrawMatrix = Matrix()
    private var scaleFactor = 1f
    private var extraTransitionX = 0f
    private var extraTransitionY = 0f

    fun updateDiagramImage(
        expBitmap: Bitmap,
        puckPosition: ExpImagePuckPosition,
        puckBitmap: Bitmap? = null,
        puckPaint: Paint? = null,
    ) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("Updating the diagram image must be done on the main thread.")
        }
        if (this.expBitmap != expBitmap &&
            expBitmap.width > 0 &&
            expBitmap.height > 0
        ) {
            this.expBitmap = expBitmap
            setImageBitmap(expBitmap)
            updateScaleFactor()
        }
        if (this.puckBitmap != puckBitmap) {
            this.puckBitmap = puckBitmap
            puckHalfWidth = (puckBitmap?.width ?: 0) / 2f
            puckHalfHeight = (puckBitmap?.height ?: 0) / 2f
        }
        this.puckPaint = puckPaint
        this.puckPosition = puckPosition
        invalidate()
    }

    private fun updateScaleFactor() {
        expBitmap?.let { bitmap ->
            scaleFactor = min(
                width / bitmap.width.toFloat(),
                height / bitmap.height.toFloat()
            )
            extraTransitionX = (width - scaleFactor * bitmap.width) / 2f
            extraTransitionY = (height - scaleFactor * bitmap.height) / 2f
        }
    }

    fun clear() {
        expBitmap = null
        puckPosition = null
        puckBitmap = null
        puckPaint = null
        setImageBitmap(null)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if ((width != oldWidth || height != oldHeight) && width > 0 && height > 0) {
            updateScaleFactor()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        ifNonNull(puckBitmap, puckPosition) { puckBitmap, puckPosition ->
            puckDrawMatrix.reset()
            if (enablePuckRotation) {
                puckDrawMatrix.postRotate(
                    puckPosition.rotationDegrees,
                    puckHalfWidth,
                    puckHalfHeight
                )
            }
            puckDrawMatrix.postScale(
                scaleFactor,
                scaleFactor
            )
            puckDrawMatrix.postTranslate(
                (puckPosition.offsetXPixels - puckHalfWidth) * scaleFactor + extraTransitionX,
                (puckPosition.offsetYPixels - puckHalfHeight) * scaleFactor + extraTransitionY
            )
            canvas.drawBitmap(puckBitmap, puckDrawMatrix, puckPaint)
        }
    }
}