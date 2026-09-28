package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.customWidgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.util.Random
import android.graphics.drawable.Drawable
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.SketchFilterUtil
import kotlin.math.cos
import kotlin.math.sin



class MagicPaintingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{

    // Bitmaps
    private var outlineBitmap: Bitmap? = null
    private var coloredBitmap: Bitmap? = null
    private var maskBitmap: Bitmap? = null
    private var maskCanvas: Canvas? = null

    private var currentColorRes: Int? = null

    // Brush Icon
    private val brushDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_magic_brush)
    private var isFingerDown = false
    private var brushX = 0f
    private var brushY = 0f
    private val brushSize = 110 // dp-scaled display size

    // Callback triggered when the child has revealed ~90% of the image
    var onPaintingCompleted: (() -> Unit)? = null
    private var isCompleted = false

    // Paints
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 40f // 👈 Reduced from 95f to make brushing slower and more precise
        color = Color.BLACK
        // Softens reveal edges for silky-smooth painting
        maskFilter = BlurMaskFilter(14f, BlurMaskFilter.Blur.NORMAL)
    }

    private val blendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val sparklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val touchPath = Path()
    private var lastX = 0f
    private var lastY = 0f

    // Sparkling Twinkle Particles
    private data class Sparkle(
        var x: Float,
        var y: Float,
        val vx: Float,
        val vy: Float,
        var alpha: Int,
        var size: Float,
        var rotation: Float,
        val rotSpeed: Float,
        val color: Int
    )

    private val sparkles = mutableListOf<Sparkle>()
    private val random = Random()
    private val sparkleColors = intArrayOf(
        Color.parseColor("#FFF176"), // Gold
        Color.parseColor("#FFD700"), // Deep Gold
        Color.parseColor("#FFFFFF"), // Sparkle White
        Color.parseColor("#FF4081"), // Magic Pink
        Color.parseColor("#00E5FF")  // Diamond Blue
    )

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w <= 0 || h <= 0) return

        initMaskCanvas(w, h)
        currentColorRes?.let { loadColoredImage(it) }
    }

    private fun initMaskCanvas(w: Int, h: Int) {
        maskBitmap?.recycle()
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        maskCanvas = Canvas(maskBitmap!!)
        maskCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun loadColoredImage(@DrawableRes colorRes: Int) {
        currentColorRes = colorRes
        if (width <= 0 || height <= 0) return

        val drawable = ContextCompat.getDrawable(context, colorRes) ?: return

        coloredBitmap?.recycle()
        coloredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val colorCanvas = Canvas(coloredBitmap!!)
        val margin = (width * 0.05f).toInt()
        drawable.setBounds(margin, margin, width - margin, height - margin)
        drawable.draw(colorCanvas)

        outlineBitmap?.recycle()
        outlineBitmap = SketchFilterUtil.createSketch(coloredBitmap!!)

        resetCanvas()
    }

    fun resetCanvas() {
        maskCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        sparkles.clear()
        isFingerDown = false
        isCompleted = false // Reset
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Black & White Base Sketch
        outlineBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }

        // 2. Smoothly Revealed Color Layer
        if (coloredBitmap != null && maskBitmap != null) {
            val checkpoint = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            canvas.drawBitmap(coloredBitmap!!, 0f, 0f, null)
            canvas.drawBitmap(maskBitmap!!, 0f, 0f, blendPaint)
            canvas.restoreToCount(checkpoint)
        }

        // 3. Floating Twinkling Stars
        renderSparkles(canvas)

        // 4. Paintbrush Follower (Only visible while finger is on screen)
        if (isFingerDown && brushDrawable != null) {
            // Exact tip alignment (Tip is located at bottom-left of the icon)
            val left = (brushX - (brushSize * 0.11f)).toInt()
            val top = (brushY - (brushSize * 0.91f)).toInt()
            brushDrawable.setBounds(left, top, left + brushSize, top + brushSize)
            brushDrawable.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        brushX = x
        brushY = y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isFingerDown = true
                touchPath.reset()
                touchPath.moveTo(x, y)
                lastX = x
                lastY = y

                maskCanvas?.drawCircle(x, y, 48f, maskPaint)
                spawnSparkles(x, y, 8)
            }
            MotionEvent.ACTION_MOVE -> {
                isFingerDown = true
                val midX = (x + lastX) / 2f
                val midY = (y + lastY) / 2f
                touchPath.quadTo(lastX, lastY, midX, midY)

                maskCanvas?.drawPath(touchPath, maskPaint)
                touchPath.reset()
                touchPath.moveTo(midX, midY)

                lastX = x
                lastY = y
                spawnSparkles(x, y, 5)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isFingerDown = false
                maskCanvas?.drawPath(touchPath, maskPaint)
                touchPath.reset()

                // Check if drawing is mostly complete
                if (!isCompleted) {
                    checkRevealProgress()
                }

            }
        }
        invalidate()
        return true
    }

    private fun checkRevealProgress() {
        val mask = maskBitmap ?: return
        val sampleGrid = 25 // 25x25 = 625 test points
        val stepX = mask.width / sampleGrid
        val stepY = mask.height / sampleGrid

        var revealedPoints = 0
        val totalPoints = sampleGrid * sampleGrid

        for (y in 0 until sampleGrid) {
            for (x in 0 until sampleGrid) {
                val pixel = mask.getPixel(x * stepX, y * stepY)
                val alpha = (pixel shr 24) and 0xFF
                if (alpha > 50) {
                    revealedPoints++
                }
            }
        }

        val progress = revealedPoints.toFloat() / totalPoints

        // 👈 Increased from 0.92f to 0.96f so they have to reveal almost the whole image
        if (progress >= 0.92f) {
            isCompleted = true
            maskCanvas?.drawColor(Color.BLACK)
            invalidate()
            onPaintingCompleted?.invoke()
        }
    }



    private fun spawnSparkles(x: Float, y: Float, count: Int) {
        for (i in 0 until count) {
            val angle = random.nextDouble() * 2 * Math.PI
            val speed = 2f + random.nextFloat() * 5f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed).toFloat()
            val color = sparkleColors[random.nextInt(sparkleColors.size)]
            val size = 7f + random.nextFloat() * 11f
            val rotSpeed = -15f + random.nextFloat() * 30f

            sparkles.add(Sparkle(x, y, vx, vy, 255, size, random.nextFloat() * 360f, rotSpeed, color))
        }
    }

    private fun renderSparkles(canvas: Canvas) {
        if (sparkles.isEmpty()) return

        val iterator = sparkles.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            s.x += s.vx
            s.y += s.vy
            s.rotation += s.rotSpeed
            s.alpha -= 9

            if (s.alpha <= 0) {
                iterator.remove()
            } else {
                sparklePaint.color = s.color
                sparklePaint.alpha = s.alpha

                canvas.save()
                canvas.translate(s.x, s.y)
                canvas.rotate(s.rotation)
                drawDiamondSparkle(canvas, s.size, sparklePaint)
                canvas.restore()
            }
        }
        postInvalidateOnAnimation()
    }

    private fun drawDiamondSparkle(canvas: Canvas, radius: Float, paint: Paint) {
        val path = Path().apply {
            moveTo(0f, -radius * 1.6f)
            quadTo(0f, 0f, radius * 1.6f, 0f)
            quadTo(0f, 0f, 0f, radius * 1.6f)
            quadTo(0f, 0f, -radius * 1.6f, 0f)
            quadTo(0f, 0f, 0f, -radius * 1.6f)
            close()
        }
        canvas.drawPath(path, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        outlineBitmap?.recycle()
        coloredBitmap?.recycle()
        maskBitmap?.recycle()
    }

    fun loadColoredBitmap(sourceBitmap: Bitmap) {
        if (width <= 0 || height <= 0) return

        val drawable = android.graphics.drawable.BitmapDrawable(resources, sourceBitmap)

        coloredBitmap?.recycle()
        coloredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val colorCanvas = Canvas(coloredBitmap!!)
        val margin = (width * 0.05f).toInt()
        drawable.setBounds(margin, margin, width - margin, height - margin)
        drawable.draw(colorCanvas)

        outlineBitmap?.recycle()
        outlineBitmap = SketchFilterUtil.createSketch(coloredBitmap!!)

        resetCanvas()
    }



}


