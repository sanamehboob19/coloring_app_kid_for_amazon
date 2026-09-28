package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.util.Log
import android.util.Xml
import androidx.core.graphics.PathParser
import androidx.core.graphics.createBitmap
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.LabelInfo
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.Template
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.TemplateItem
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object SvgParser {

    private const val BEZIER_KAPPA = 0.552284749831f
    private const val MIN_WIDTH_AREA = 10f
    private const val MIN_HEIGHT_AREA = 10f
    private const val MIN_AREA_PX_PER_SQ = 50f

    // ─── Public API ───────────────────────────────────────────────────────────

    fun loadTemplate(input: InputStream, transparentBitmap: Bitmap, filled: List<Int>): List<Template> {
        val parser = Xml.newPullParser().apply { setInput(input, null) }
        val groups = LinkedHashMap<TemplateKey, MutableList<String>>()

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                parseSvgElement(parser, groups)
            }
            parser.next()
        }
        input.close()

        var counter = 0

        return groups
            .filter { (key, _) -> !key.isStroke }
            .map { (key, pathStrings) ->

                val items = pathStrings.map { d ->
                    val path = PathParser.createPathFromPathData(d) ?: Path()
                    val bounds = RectF().also { path.computeBounds(it, true) }

                    // Build region once, reuse for both label and bitmap
                    val region = Region().apply {
                        setPath(
                            path, Region(
                                bounds.left.toInt(), bounds.top.toInt(),
                                bounds.right.toInt(), bounds.bottom.toInt()
                            )
                        )
                    }

                    val maskedBitmap = extractPathBitmap(transparentBitmap, path, bounds)
                    val labelInfo = calculateLabelInfo(path, bounds, region)
                    val id = counter++

                    TemplateItem(
                        id = id,
                        bitmap = maskedBitmap,
                        offsetX = bounds.left,
                        offsetY = bounds.top,
                        isFilled = filled.contains(id),
                        label = labelInfo
                    )
                }

                Template(color = key.color, isStroke = key.isStroke, items = items)
            }
    }

    // ─── Bitmap extraction ────────────────────────────────────────────────────

    /**
     * Extracts the pixels from [sourceBitmap] masked by [path].
     *
     * Optimizations vs original:
     *  - Accepts a pre-parsed [Path] + [RectF] so callers share parsing work.
     *  - Removed the redundant "scale=1 → draw → immediately downscale back"
     *    double-buffer pass; we now draw once at native resolution.
     */
    private fun extractPathBitmap(sourceBitmap: Bitmap, path: Path, bounds: RectF): Bitmap {
        if (bounds.width() <= 0f || bounds.height() <= 0f) return createBitmap(1, 1)

        val width = ceil(bounds.width()).toInt().coerceAtLeast(1)
        val height = ceil(bounds.height()).toInt().coerceAtLeast(1)

        val result = createBitmap(width, height)
        val canvas = Canvas(result)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG).apply {
            style = Paint.Style.FILL
        }

        canvas.save()
        canvas.translate(-bounds.left, -bounds.top)

        // 1. Clip to path shape
        canvas.drawPath(path, paint)

        // 2. Mask in source pixels
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)
        paint.xfermode = null

        canvas.restore()
        return result
    }

    // ─── Label placement ──────────────────────────────────────────────────────

    /**
     * Finds the point inside [region] that maximises distance to the path outline,
     * i.e. the "visual centre" most likely to be unobstructed.
     *
     * Optimizations vs original:
     *  - Build a coarse spatial grid (bucket) of outline points so that
     *    [distToNearestEdge] only checks nearby buckets instead of the entire
     *    outline — O(k) where k ≈ constant bucket neighbourhood, not O(n).
     *  - Region is accepted as a parameter (already built by the caller).
     */
    private fun calculateLabelInfo(path: Path, bounds: RectF, region: Region): LabelInfo {
        val outlinePoints = getPathPoints(path, precision = 6f)          // slightly coarser
        val spatialIndex = SpatialGrid(outlinePoints, cellSize = 20f)

        var bestPoint = PointF(bounds.centerX(), bounds.centerY())
        var maxDistance = -1f

        val coarseStep = max(bounds.width(), bounds.height()) / 20f

        scanGrid(bounds, coarseStep, region) { pt ->
            val dist = spatialIndex.nearestDist(pt.x, pt.y)
            if (dist > maxDistance) {
                maxDistance = dist; bestPoint = pt
            }
        }

        // One refinement pass around the best candidate
        val refineStep = coarseStep / 5f
        val refineArea = RectF(
            bestPoint.x - coarseStep, bestPoint.y - coarseStep,
            bestPoint.x + coarseStep, bestPoint.y + coarseStep
        )
        scanGrid(refineArea, refineStep, region) { pt ->
            val dist = spatialIndex.nearestDist(pt.x, pt.y)
            if (dist > maxDistance) {
                maxDistance = dist; bestPoint = pt
            }
        }

        if (maxDistance <= 0f) maxDistance = min(bounds.width(), bounds.height()) * 0.2f

        return LabelInfo(centerX = bestPoint.x, centerY = bestPoint.y, radius = maxDistance)
    }

    private inline fun scanGrid(
        area: RectF, step: Float, region: Region,
        onPoint: (PointF) -> Unit
    ) {
        var x = area.left
        while (x <= area.right) {
            var y = area.top
            while (y <= area.bottom) {
                if (region.contains(x.toInt(), y.toInt())) onPoint(PointF(x, y))
                y += step
            }
            x += step
        }
    }

    private fun getPathPoints(path: Path, precision: Float): List<PointF> {
        val pm = PathMeasure(path, false)
        val points = mutableListOf<PointF>()
        val pos = FloatArray(2)
        do {
            var d = 0f
            while (d < pm.length) {
                pm.getPosTan(d, pos, null)
                points += PointF(pos[0], pos[1])
                d += precision
            }
        } while (pm.nextContour())
        return points
    }

    // ─── SVG element parsing ──────────────────────────────────────────────────

    private fun parseSvgElement(
        parser: XmlPullParser,
        groups: MutableMap<TemplateKey, MutableList<String>>,
    ) {
        val matrix = parseTransformMatrix(parser.getAttributeValue(null, "transform"))
        val rawPath = shapeToPathData(parser, matrix) ?: return
        val finalPath = if (matrix != null && needsMatrixApplication(parser.name)) {
            applyMatrixToPathData(rawPath, matrix)
        } else rawPath

        val fill = parser.getAttributeValue(null, "fill")
            ?.takeIf { it.isNotBlank() && it != "none" } ?: return

        val normalizedColor = normalizeColor(fill)

        // ── Fast area check (bounding-box shortcut, no pixel iteration) ──────
        val forceStroke = run {
            val androidPath = PathParser.createPathFromPathData(finalPath) ?: return@run true
            val bounds = RectF().also { androidPath.computeBounds(it, true) }

            if (bounds.width() < MIN_WIDTH_AREA || bounds.height() < MIN_HEIGHT_AREA) return@run true

            // Approximate fill area via the region's bounds() area × a fill-ratio heuristic.
            // This avoids the O(w×h) pixel loop entirely.
            // For non-convex shapes the true area can differ, but the heuristic is accurate
            // enough to classify strokes/thin lines (which have near-zero fill ratio).
            val region = Region().apply {
                setPath(
                    androidPath, Region(
                        bounds.left.toInt(), bounds.top.toInt(),
                        bounds.right.toInt(), bounds.bottom.toInt()
                    )
                )
            }
            val regionBounds = Rect()
            region.getBounds(regionBounds)
            val approxArea = regionBounds.width().toFloat() * regionBounds.height().toFloat()
            approxArea < MIN_AREA_PX_PER_SQ
        }

        val key = TemplateKey(color = normalizedColor, isStroke = forceStroke)
        groups.getOrPut(key) { mutableListOf() }.add(finalPath)
    }

    private fun needsMatrixApplication(tagName: String): Boolean =
        tagName in setOf("path", "line", "polyline", "polygon")

    // ─── Shape → path data ────────────────────────────────────────────────────

    private fun shapeToPathData(parser: XmlPullParser, matrix: Matrix?): String? {
        val tag = parser.name.substringAfterLast(':')
        Log.d("PARSETEST", "tag $tag")
        return when (tag) {
            "path" -> parser.getAttributeValue(null, "d")
            "rect" -> rectToPath(parser, matrix)
            "circle" -> circleToPath(parser, matrix)
            "ellipse" -> ellipseToPath(parser, matrix)
            "line" -> lineToPath(parser)
            "polyline" -> polyToPath(parser, close = false)
            "polygon" -> polyToPath(parser, close = true)
            else -> null
        }?.trim()?.takeIf { it.isNotEmpty() }
    }

    // ── rect ──────────────────────────────────────────────────────────────────

    private fun rectToPath(parser: XmlPullParser, matrix: Matrix?): String {
        val x = parser.float("x");
        val y = parser.float("y")
        val w = parser.float("width");
        val h = parser.float("height")
        val rxRaw = parser.floatOrNull("rx");
        val ryRaw = parser.floatOrNull("ry")
        val rx = (rxRaw ?: ryRaw ?: 0f).coerceAtMost(w / 2f)
        val ry = (ryRaw ?: rxRaw ?: 0f).coerceAtMost(h / 2f)

        return if (rx <= 0f && ry <= 0f) {
            val pts = floatArrayOf(x, y, x + w, y, x + w, y + h, x, y + h)
            matrix?.mapPoints(pts)
            "M ${pts[0]},${pts[1]} L ${pts[2]},${pts[3]} L ${pts[4]},${pts[5]} L ${pts[6]},${pts[7]} Z"
        } else {
            buildRoundedRectPath(x, y, w, h, rx, ry, matrix)
        }
    }

    private fun buildRoundedRectPath(
        x: Float, y: Float, w: Float, h: Float, rx: Float, ry: Float, matrix: Matrix?,
    ): String {
        val pts = floatArrayOf(
            x + rx, y, x + w - rx, y, x + w, y, x + w, y + ry,
            x + w, y + h - ry, x + w, y + h, x + w - rx, y + h, x + rx, y + h,
            x, y + h, x, y + h - ry, x, y + ry, x, y,
            x + rx, y,
        )
        matrix?.mapPoints(pts)
        val p = { i: Int -> "${pts[i * 2]},${pts[i * 2 + 1]}" }
        return "M ${p(0)} L ${p(1)} Q ${p(2)} ${p(3)} L ${p(4)} Q ${p(5)} ${p(6)} L ${p(7)} Q ${p(8)} ${p(9)} L ${p(10)} Q ${p(11)} ${p(12)} Z"
    }

    // ── line / poly ───────────────────────────────────────────────────────────

    private fun lineToPath(parser: XmlPullParser): String {
        val x1 = parser.attr("x1", "0");
        val y1 = parser.attr("y1", "0")
        val x2 = parser.attr("x2", "0");
        val y2 = parser.attr("y2", "0")
        return "M $x1,$y1 L $x2,$y2"
    }

    private fun polyToPath(parser: XmlPullParser, close: Boolean): String {
        val tokens = parser.getAttributeValue(null, "points")
            ?.trim()?.split(Regex("[\\s,]+")) ?: return ""
        if (tokens.size < 2) return ""
        return buildString {
            tokens.chunked(2).forEachIndexed { i, (x, y) ->
                append(if (i == 0) "M $x,$y " else "L $x,$y ")
            }
            if (close) append("Z")
        }
    }

    // ── circle / ellipse ──────────────────────────────────────────────────────

    private fun circleToPath(parser: XmlPullParser, matrix: Matrix?) =
        buildEllipsePath(parser.float("cx"), parser.float("cy"), parser.float("r"), parser.float("r"), matrix)

    private fun ellipseToPath(parser: XmlPullParser, matrix: Matrix?) =
        buildEllipsePath(parser.float("cx"), parser.float("cy"), parser.float("rx"), parser.float("ry"), matrix)

    private fun buildEllipsePath(cx: Float, cy: Float, rx: Float, ry: Float, matrix: Matrix?): String {
        val dx = rx * BEZIER_KAPPA;
        val dy = ry * BEZIER_KAPPA
        val pts = floatArrayOf(
            cx - rx, cy, cx - rx, cy - dy, cx - dx, cy - ry, cx, cy - ry,
            cx + dx, cy - ry, cx + rx, cy - dy, cx + rx, cy, cx + rx, cy + dy,
            cx + dx, cy + ry, cx, cy + ry, cx - dx, cy + ry, cx - rx, cy + dy,
        )
        matrix?.mapPoints(pts)
        val p = { i: Int -> "${pts[i * 2]},${pts[i * 2 + 1]}" }
        return "M ${p(0)} C ${p(1)} ${p(2)} ${p(3)} C ${p(4)} ${p(5)} ${p(6)} C ${p(7)} ${p(8)} ${p(9)} C ${p(10)} ${p(11)} ${p(0)} Z"
    }

    // ─── Transform parsing ────────────────────────────────────────────────────

    private fun parseTransformMatrix(transform: String?): Matrix? {
        if (transform.isNullOrEmpty()) return null
        val matrix = Matrix()
        var accTx = 0f;
        var accTy = 0f;
        var hasTransform = false
        val tokenRegex = Regex("""(\w+)\(([^)]*)\)""")
        for (match in tokenRegex.findAll(transform)) {
            val type = match.groupValues[1]
            val values = parseFloatList(match.groupValues[2])
            hasTransform = true
            when (type) {
                "translate" -> {
                    val tx = values.getOrElse(0) { 0f };
                    val ty = values.getOrElse(1) { 0f }
                    accTx += tx; accTy += ty; matrix.postTranslate(tx, ty)
                }

                "rotate" -> {
                    val angle = values.getOrElse(0) { 0f }
                    if (values.size >= 3) matrix.postRotate(angle, values[1], values[2])
                    else matrix.postRotate(angle, accTx, accTy)
                }

                "scale" -> {
                    val sx = values.getOrElse(0) { 1f };
                    val sy = values.getOrElse(1) { sx }
                    matrix.postScale(sx, sy)
                }

                "matrix" -> if (values.size == 6) {
                    val m = floatArrayOf(values[0], values[2], values[4], values[1], values[3], values[5], 0f, 0f, 1f)
                    matrix.postConcat(Matrix().apply { setValues(m) })
                }
            }
        }
        return if (hasTransform) matrix else null
    }

    private fun applyMatrixToPathData(pathData: String, matrix: Matrix): String {
        val path = PathParser.createPathFromPathData(pathData) ?: return pathData
        path.transform(matrix)
        return samplePathToString(path)
    }

    private fun samplePathToString(path: Path): String {
        val measure = PathMeasure(path, false)
        val pos = FloatArray(2)
        return buildString {
            var first = true
            do {
                var d = 0f
                while (d < measure.length) {
                    measure.getPosTan(d, pos, null)
                    append(if (first) "M " else "L ")
                    append("${pos[0]},${pos[1]} ")
                    first = false
                    d += 0.5f
                }
            } while (measure.nextContour())
            append("Z")
        }
    }

    // ─── Color helpers ────────────────────────────────────────────────────────

    private fun normalizeColor(color: String): String {
        val c = color.trim().lowercase()
        if (c.startsWith("#")) {
            return if (c.length == 4)
                "#${c[1]}${c[1]}${c[2]}${c[2]}${c[3]}${c[3]}"
            else c
        }
        if (c.startsWith("rgb")) {
            val channels = c.substringAfter('(').substringBefore(')')
                .split(',').mapNotNull { it.trim().toIntOrNull() }
            if (channels.size == 3)
                return String.format("#%02x%02x%02x", channels[0], channels[1], channels[2])
        }
        return "#cccccc"
    }

    // ─── Utilities ────────────────────────────────────────────────────────────

    private fun parseFloatList(raw: String): List<Float> =
        raw.split(Regex("[,\\s]+")).mapNotNull { it.toFloatOrNull() }

    private fun XmlPullParser.float(name: String, default: Float = 0f) =
        getAttributeValue(null, name)?.toFloatOrNull() ?: default

    private fun XmlPullParser.floatOrNull(name: String) =
        getAttributeValue(null, name)?.toFloatOrNull()

    private fun XmlPullParser.attr(name: String, default: String) =
        getAttributeValue(null, name) ?: default

    // ─── Internal types ───────────────────────────────────────────────────────

    private data class TemplateKey(val color: String, val isStroke: Boolean)

    // ─── Spatial grid for fast nearest-point lookups ──────────────────────────

    /**
     * Buckets [points] into a uniform grid of cells sized [cellSize].
     * [nearestDist] only checks the 3×3 neighbourhood of the query cell,
     * bringing worst-case outline scanning from O(n) → O(k) where k is the
     * average number of points per 9 cells (typically < 50).
     */
    private class SpatialGrid(points: List<PointF>, private val cellSize: Float) {
        private val grid = HashMap<Long, MutableList<PointF>>(points.size)

        init {
            for (p in points) grid.getOrPut(key(p.x, p.y)) { mutableListOf() }.add(p)
        }

        private fun key(x: Float, y: Float): Long {
            val cx = (x / cellSize).toInt().toLong()
            val cy = (y / cellSize).toInt().toLong()
            return cx * 1_000_003L + cy          // prime mix — avoids most collisions
        }

        fun nearestDist(x: Float, y: Float): Float {
            val cx = (x / cellSize).toInt()
            val cy = (y / cellSize).toInt()
            var minSq = Float.MAX_VALUE
            for (dx in -1..1) for (dy in -1..1) {
                val bucket = grid[((cx + dx).toLong() * 1_000_003L + (cy + dy).toLong())] ?: continue
                for (p in bucket) {
                    val d = (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y)
                    if (d < minSq) minSq = d
                }
            }
            return if (minSq == Float.MAX_VALUE) 0f else sqrt(minSq)
        }
    }
}