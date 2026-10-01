package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.FloodFillUtil
import java.util.Stack
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point



class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{




    enum class ToolMode {
        BUCKET,  // Tap to fill inside boundaries
        BRUSH,   // Freehand smooth painting
        GLOW,    // Neon magic glowing brush
        ERASER   // Clean paint
    }

    // Drawing settings
    var currentTool: ToolMode = ToolMode.BUCKET
    var currentColor: Int = Color.RED
        set(value) {
            field = value
            updatePaintColor()
        }
    var currentStrokeWidth: Float = 36f

    // Toggle: true for dark/neon canvas (adds white laser core), false for normal coloring
    var isNeonCoreEnabled: Boolean = false

    // Canvas & Bitmaps
    private var canvasBackgroundColor: Int = Color.WHITE
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private var templateBitmap: Bitmap? = null
    private var currentTemplateRes: Int? = null

    // Drawing Paints & Paths
    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val innerGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = Color.WHITE
    }

    private val canvasPaint = Paint(Paint.DITHER_FLAG)
    private val drawPath = Path()
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    // Undo history stack (limit to 6 states to prevent Out-Of-Memory)
    private val undoStack = Stack<Bitmap>()
    private val maxUndoSteps = 6

    init {
        // Software layer required for BlurMaskFilter glow effects
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updatePaintColor()
    }

    private fun updatePaintColor() {
        drawPaint.color = currentColor
    }

    fun setCanvasBackgroundColor(color: Int) {
        canvasBackgroundColor = color
        resetCanvas()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0 || h <= 0) return

        initBitmapCanvas(w, h)
        currentTemplateRes?.let { loadTemplate(it) }
    }

    private fun initBitmapCanvas(w: Int, h: Int) {
        canvasBitmap?.recycle()
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        drawCanvas?.drawColor(canvasBackgroundColor)
        undoStack.clear()
    }

    /**
     * Loads and fits the outline template onto the drawing canvas.
     */
    fun loadTemplate(@DrawableRes resId: Int) {
        currentTemplateRes = resId
        if (width <= 0 || height <= 0) return

        val drawable = ContextCompat.getDrawable(context, resId) ?: return

        templateBitmap?.recycle()
        templateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tCanvas = Canvas(templateBitmap!!)

        val margin = (width * 0.08f).toInt()
        drawable.setBounds(margin, margin, width - margin, height - margin)
        drawable.draw(tCanvas)

        resetCanvas()
    }

    /**
     * Resets canvas using current background color and template.
     */
    fun resetCanvas() {
        drawCanvas?.drawColor(canvasBackgroundColor)
        templateBitmap?.let { drawCanvas?.drawBitmap(it, 0f, 0f, null) }
        undoStack.clear()
        saveUndoState()
        invalidate()
    }

    private fun saveUndoState() {
        canvasBitmap?.let {
            if (undoStack.size >= maxUndoSteps) {
                undoStack.removeAt(0).recycle()
            }
            undoStack.push(it.copy(it.config ?: Bitmap.Config.ARGB_8888, true))
        }
    }

    fun undo() {
        if (undoStack.size > 1) {
            undoStack.pop().recycle()
            val previousState = undoStack.peek()

            drawCanvas?.drawColor(canvasBackgroundColor)
            drawCanvas?.drawBitmap(previousState, 0f, 0f, null)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, canvasPaint) }

        // Live preview while finger is dragging
        if (currentTool != ToolMode.BUCKET && !drawPath.isEmpty) {
            canvas.drawPath(drawPath, drawPaint)
            if (currentTool == ToolMode.GLOW && isNeonCoreEnabled) {
                canvas.drawPath(drawPath, innerGlowPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (currentTool) {
            ToolMode.BUCKET -> {
                if (event.action == MotionEvent.ACTION_UP) {
                    handleBucketFill(x.toInt(), y.toInt())
                }
            }
            ToolMode.BRUSH, ToolMode.GLOW, ToolMode.ERASER -> {
                handleBrushStroke(event, x, y)
            }
        }
        return true
    }

    private fun handleBucketFill(x: Int, y: Int) {
        val bitmap = canvasBitmap ?: return
        saveUndoState()

        FloodFillUtil.floodFill(bitmap, Point(x, y), currentColor)

        // Redraw outline on top so borders remain sharp
        templateBitmap?.let { drawCanvas?.drawBitmap(it, 0f, 0f, null) }
        invalidate()
    }

    private fun handleBrushStroke(event: MotionEvent, x: Float, y: Float) {
        configureBrushPaint()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                saveUndoState()
                drawPath.reset()
                drawPath.moveTo(x, y)
                lastTouchX = x
                lastTouchY = y
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val midX = (x + lastTouchX) / 2f
                val midY = (y + lastTouchY) / 2f
                drawPath.quadTo(lastTouchX, lastTouchY, midX, midY)
                lastTouchX = x
                lastTouchY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, drawPaint)
                if (currentTool == ToolMode.GLOW && isNeonCoreEnabled) {
                    drawCanvas?.drawPath(drawPath, innerGlowPaint)
                }
                drawPath.reset()

                // Redraw black outline on top of strokes
                templateBitmap?.let { drawCanvas?.drawBitmap(it, 0f, 0f, null) }
                invalidate()
            }
        }
    }

    private fun configureBrushPaint() {
        drawPaint.strokeWidth = currentStrokeWidth

        when (currentTool) {
            ToolMode.BRUSH -> {
                drawPaint.color = currentColor
                drawPaint.maskFilter = null
            }
            ToolMode.GLOW -> {
                drawPaint.color = currentColor
                if (isNeonCoreEnabled) {
                    // Dark neon canvas: crisp glow with inner laser core
                    drawPaint.maskFilter = BlurMaskFilter(18f, BlurMaskFilter.Blur.SOLID)
                    innerGlowPaint.strokeWidth = currentStrokeWidth * 0.35f
                } else {
                    // Coloring book: original soft colorful glow
                    drawPaint.maskFilter = BlurMaskFilter(24f, BlurMaskFilter.Blur.SOLID)
                }
            }
            ToolMode.ERASER -> {
                // Uses current canvas background (White for coloring, Black for glow pen)
                drawPaint.color = canvasBackgroundColor
                drawPaint.maskFilter = null
            }
            ToolMode.BUCKET -> Unit
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        canvasBitmap?.recycle()
        templateBitmap?.recycle()
        while (undoStack.isNotEmpty()) {
            undoStack.pop().recycle()
        }
    }

    /**
     *
     * Exports a clean snapshot copy of the current drawing.
     */
    fun getCanvasBitmap(): Bitmap? {
        return canvasBitmap?.copy(Bitmap.Config.ARGB_8888, false)
    }



    /**
     * Loads and scales a pre-downloaded Bitmap template onto the drawing canvas.
     */
    fun loadTemplateBitmap(sourceBitmap: Bitmap) {
        if (width <= 0 || height <= 0) return

        templateBitmap?.recycle()
        templateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tCanvas = Canvas(templateBitmap!!)

        val margin = (width * 0.08f).toInt()
        val destRect = android.graphics.Rect(margin, margin, width - margin, height - margin)

        // Scale and draw the bitmap centered with margins
        tCanvas.drawBitmap(sourceBitmap, null, destRect, null)

        resetCanvas()
    }




}