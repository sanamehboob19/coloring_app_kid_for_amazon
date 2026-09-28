package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.Log
import com.caverock.androidsvg.SVG
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.DownloadConfig
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.sealed.DownloadState
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.extension.showLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.cancellation.CancellationException

class SvgDownloadManager(
    private val context: Context,
    private val config: DownloadConfig = DownloadConfig(),
)
{

    // ── OkHttp client ────────────────────────────────────────────

    private val client = OkHttpClient.Builder()
        .connectTimeout(config.connectTimeoutSec, TimeUnit.SECONDS)
        .readTimeout(config.readTimeoutSec, TimeUnit.SECONDS)
        .writeTimeout(config.writeTimeoutSec, TimeUnit.SECONDS)
        .build()

    // ── Internal state ───────────────────────────────────────────

    private val _state = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val state: StateFlow<DownloadState> = _state.asStateFlow()

    private var downloadJob: Job? = null
    private var currentUrl: String? = null

    private val isPaused = AtomicBoolean(false)
    private val isCancelled = AtomicBoolean(false)
    private val downloadedBytes = AtomicLong(0L)

    // ── Cache dirs ───────────────────────────────────────────────

    private val svgCacheDir = File(context.cacheDir, "svgs").apply { mkdirs() }
    private val bmpCacheDir = File(context.cacheDir, "bitmaps").apply { mkdirs() }
    private val tempCacheDir = File(context.cacheDir, "temp").apply { mkdirs() }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────

    /**
     * Start a new download. If the same URL is already cached, returns
     * immediately with [DownloadState.Success].
     */
    fun download(url: String, scope: CoroutineScope) {
        // Cancel any existing job first
        cancelInternal()

        currentUrl = url
        isPaused.set(false)
        isCancelled.set(false)
        downloadedBytes.set(0L)

        downloadJob = scope.launch(Dispatchers.IO) {
            downloadWithRetry(url)
        }
    }

    fun downloadAndParse(
        url: String,
        transparentBitmap: Bitmap,
        sourceBitmap: Bitmap,
        filled: List<Int>,
        scope: CoroutineScope
    ) {
        cancelInternal()

        currentUrl = url
        isPaused.set(false)
        isCancelled.set(false)
        downloadedBytes.set(0L)

        downloadJob = scope.launch(Dispatchers.IO) {
            try {
                // Phase 1: Download + render bitmap (existing logic)
                downloadWithRetry(url)

                // Phase 2: Parse templates if download succeeded
                val successState = _state.value
                if (successState is DownloadState.Success) {
                    parseTemplates(
                        context = context,
                        svgBytes = successState.svgBytes,
                        transparentBitmap = transparentBitmap,
                        sourceBitmap = sourceBitmap,
                        filled = filled,
                        existingSuccess = successState
                    )
                }

            } catch (e: CancellationException) {
                _state.value = DownloadState.Cancelled
            } catch (e: Exception) {
                _state.value = DownloadState.Failed(
                    error = e.message ?: "Unknown error",
                    throwable = e
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — PARSE TEMPLATES
    // ─────────────────────────────────────────────────────────────

    private suspend fun parseTemplates(
        context: Context,
        svgBytes: ByteArray,
        transparentBitmap: Bitmap,
        sourceBitmap: Bitmap,
        filled: List<Int>,
        existingSuccess: DownloadState.Success
    ) {
        // Step 1
        _state.value = DownloadState.ParsingProgress(
            message = "Loading...",
            step = 1,
            totalSteps = 4
        )

        if (isCancelled.get()) {
            _state.value = DownloadState.Cancelled; return
        }

        // Step 2
        _state.value = DownloadState.ParsingProgress(
            message = "Loading...",
            step = 2,
            totalSteps = 4
        )

        if (isCancelled.get()) {
            _state.value = DownloadState.Cancelled; return
        }

        // Step 3 — actual parsing (runs on Default dispatcher, can be slow)
        _state.value = DownloadState.ParsingProgress(
            message = "Loading...",
            step = 3,
            totalSteps = 4
        )

        val templates = withContext(Dispatchers.Default) {
            SvgParser.loadTemplate(
                input = svgBytes.inputStream(),
                transparentBitmap = transparentBitmap,
                filled = filled
            )
        }

        if (isCancelled.get()) {
            _state.value = DownloadState.Cancelled; return
        }

        // Step 4
        _state.value = DownloadState.ParsingProgress(
            message = "Finalizing...",
            step = 4,
            totalSteps = 4
        )

        // Emit final success with templates
        _state.value = DownloadState.Parsed(
            svgBytes = existingSuccess.svgBytes,
            templates = templates,
            filledIds = filled
        )
    }

    /** Pause an in-progress download. Resumes from byte offset. */
    fun pause() {
        if (_state.value is DownloadState.Downloading) {
            isPaused.set(true)
            _state.value = DownloadState.Paused
        }
    }

    /** Resume a paused download. */
    fun resume(scope: CoroutineScope) {
        if (_state.value !is DownloadState.Paused) return
        val url = currentUrl ?: return

        isPaused.set(false)
        isCancelled.set(false)

        downloadJob = scope.launch(Dispatchers.IO) {
            downloadWithRetry(url)
        }
    }

    /** Cancel and clean up temp files. */
    fun cancel() {
        cancelInternal()
        _state.value = DownloadState.Cancelled
    }

    /** Clear all disk caches. */
    fun clearCache() {
        svgCacheDir.deleteRecursively().also { svgCacheDir.mkdirs() }
        bmpCacheDir.deleteRecursively().also { bmpCacheDir.mkdirs() }
        tempCacheDir.deleteRecursively().also { tempCacheDir.mkdirs() }
    }

    /** Check if a URL is already fully cached. */
    fun isCached(url: String): Boolean = svgFileFor(url).exists() && svgFileFor(url).length() > 0

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — DOWNLOAD WITH RETRY
    // ─────────────────────────────────────────────────────────────

    private suspend fun downloadWithRetry(url: String) {
        var attempt = 0
        var lastError: Exception? = null

        while (attempt <= config.maxRetries) {
            // Check cancellation before each attempt
            if (isCancelled.get()) {
                _state.value = DownloadState.Cancelled
                return
            }

            try {
                downloadInternal(url)
                return // Success — exit retry loop
            } catch (e: CancellationException) {
                throw e // Always rethrow cancellation
            } catch (e: PauseException) {
                return // Paused intentionally — don't retry
            } catch (e: Exception) {
                lastError = e
                attempt++

                if (attempt <= config.maxRetries) {
                    _state.value = DownloadState.Downloading(
                        progress = 0,
                        downloadedBytes = 0L,
                        totalBytes = 0L
                    )
                    delay(config.retryDelayMs * attempt) // Exponential-ish backoff
                }
            }
        }

        // All retries exhausted
        _state.value = DownloadState.Failed(
            error = "Download failed after ${config.maxRetries} retries: ${lastError?.message}",
            throwable = lastError
        )
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — CORE DOWNLOAD
    // ─────────────────────────────────────────────────────────────

    private suspend fun getTotalBytes(url: String): Long {
        return try {
            val headRequest = Request.Builder()
                .url(url)
                .head() // HEAD request — no body, just headers
                .build()

            client.newCall(headRequest).execute().use { response ->
                // Try Content-Length first
                response.header("Content-Length")?.toLongOrNull()
                // Some servers use different headers
                    ?: response.header("X-Content-Length")?.toLongOrNull()
                    ?: response.header("X-File-Size")?.toLongOrNull()
                    ?: -1L
            }
        } catch (e: Exception) {
            Log.e("DOWNLOAD", "HEAD request failed: ${e.message}")
            -1L
        }
    }

    private suspend fun downloadInternal(url: String) {
        val ESTIMATED_TOTAL = 300 * 1024L // 300KB fallback

        // ── 1. Check cache ────────────────────────────────────────────
        val svgFile = svgFileFor(url)
        if (svgFile.exists() && svgFile.length() > 0) {
            processCachedFiles(url, svgFile)
            return
        }

        // ── 2. HEAD request to get total size upfront ─────────────────
        val headTotal = getTotalBytes(url)
        showLog("DOWNLOAD", "HEAD total: $headTotal bytes")

        // ── 3. Build GET request with Range header for resume ─────────
        val resumeFrom = downloadedBytes.get()
        val tempFile = tempFileFor(url)

        val requestBuilder = Request.Builder()
            .url(url)
            .header("Accept-Encoding", "identity") // Disable gzip for accurate byte count
        if (resumeFrom > 0 && tempFile.exists()) {
            requestBuilder.header("Range", "bytes=$resumeFrom-")
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (!response.isSuccessful && response.code != 206) {
            throw IOException("Server returned ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw IOException("Empty response body")

        // ── 4. Resolve total bytes from all possible sources ──────────
        val knownTotal: Long = when {
            headTotal > 0 -> headTotal
            response.code == 206 -> {
                response.header("Content-Range")
                    ?.substringAfterLast("/")
                    ?.toLongOrNull()
                    ?: (resumeFrom + body.contentLength()).takeIf { it > 0 }
                    ?: ESTIMATED_TOTAL
            }

            body.contentLength() > 0 -> body.contentLength()
            response.header("X-Content-Length") != null -> {
                response.header("X-Content-Length")?.toLongOrNull() ?: ESTIMATED_TOTAL
            }

            else -> ESTIMATED_TOTAL // ✅ Fallback to 300KB estimate
        }

        showLog("DOWNLOAD", "knownTotal: $knownTotal, resumeFrom: $resumeFrom, code: ${response.code}")

        // ── 5. Open output stream (append if resuming) ────────────────
        val isPartial = response.code == 206
        val outputStream = if (isPartial && tempFile.exists()) {
            FileOutputStream(tempFile, true)
        } else {
            tempFile.delete()
            downloadedBytes.set(0L)
            FileOutputStream(tempFile)
        }

        // ── 6. Stream bytes with progress ─────────────────────────────
        body.byteStream().use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    // Pause checkpoint
                    if (isPaused.get()) {
                        output.flush()
                        downloadedBytes.set(tempFile.length())
                        throw PauseException()
                    }

                    // Cancel checkpoint
                    if (isCancelled.get()) {
                        tempFile.delete()
                        throw CancellationException("Download cancelled")
                    }

                    output.write(buffer, 0, bytesRead)
                    val currentDownloaded = downloadedBytes.addAndGet(bytesRead.toLong())

                    // Cap at 99% — 100% emitted only after file fully written
                    val progress = ((currentDownloaded.toFloat() / knownTotal) * 100)
                        .toInt()
                        .coerceIn(0, 99)

                    _state.value = DownloadState.Downloading(
                        progress = progress,
                        downloadedBytes = currentDownloaded,
                        totalBytes = knownTotal
                    )

                    showLog("DOWNLOAD", "Progress: $progress%, $currentDownloaded / $knownTotal bytes")
                }
                output.flush()
            }
        }

        // ── 7. Move temp → cache ──────────────────────────────────────
        tempFile.copyTo(svgFile, overwrite = true)
        tempFile.delete()

        // ── 8. Emit true 100% before processing ───────────────────────
        _state.value = DownloadState.Downloading(
            progress = 100,
            downloadedBytes = svgFile.length(),
            totalBytes = svgFile.length()
        )

        showLog("DOWNLOAD", "Download complete: ${svgFile.length()} bytes saved")

        processCachedFiles(url, svgFile)
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — PROCESS SVG → BITMAP
    // ─────────────────────────────────────────────────────────────

    private suspend fun processCachedFiles(url: String, svgFile: File) {
        val svgBytes = svgFile.readBytes()

        // ── Check bitmap cache ────────────────────────────────────
        val bmpFile = bitmapFileFor(url)
        val bitmap: Bitmap

        if (bmpFile.exists() && bmpFile.length() > 0) {
            _state.value = DownloadState.Processing("Loading...")
            bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(bmpFile.absolutePath)
            } ?: throw IOException("Failed to decode cached bitmap")

        } else {
            _state.value = DownloadState.Processing("Loading...")
            bitmap = withContext(Dispatchers.Default) {
                renderSvgToBitmap(svgBytes)
            } ?: throw IOException("Failed to render SVG to bitmap")

            // Save bitmap cache
            _state.value = DownloadState.Processing("Saving...")
            withContext(Dispatchers.IO) {
                FileOutputStream(bmpFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
            }
        }

        _state.value = DownloadState.Success(svgBytes = svgBytes, null)
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — SVG RENDER
    // ─────────────────────────────────────────────────────────────

    private fun renderSvgToBitmap(svgBytes: ByteArray): Bitmap? {
        return try {
            val svg = SVG.getFromInputStream(svgBytes.inputStream())

            val docW = if (svg.documentWidth > 0) svg.documentWidth else svg.documentViewBox.width()
            val docH = if (svg.documentHeight > 0) svg.documentHeight else svg.documentViewBox.height()

            val ssScale = 1
            val ssW = (docW * ssScale).toInt().coerceAtLeast(1)
            val ssH = (docH * ssScale).toInt().coerceAtLeast(1)

            val config = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                Bitmap.Config.RGBA_F16 else Bitmap.Config.ARGB_8888

            val ssBitmap = Bitmap.createBitmap(ssW, ssH, config)
            val ssCanvas = Canvas(ssBitmap)
            ssCanvas.scale(ssW / docW, ssH / docH)
            svg.renderToCanvas(ssCanvas)

            val finalBitmap = Bitmap.createBitmap(docW.toInt(), docH.toInt(), Bitmap.Config.ARGB_8888)
            val downCanvas = Canvas(finalBitmap)
            val paint = Paint(
                Paint.ANTI_ALIAS_FLAG or
                        Paint.FILTER_BITMAP_FLAG or
                        Paint.DITHER_FLAG
            )
            downCanvas.drawBitmap(
                ssBitmap,
                Rect(0, 0, ssW, ssH),
                RectF(0f, 0f, docW, docH),
                paint
            )
            ssBitmap.recycle()
            finalBitmap

        } catch (e: Exception) {
            null
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────

    private fun cancelInternal() {
        isCancelled.set(true)
        downloadJob?.cancel()
        downloadJob = null
    }

    private fun svgFileFor(url: String) = File(svgCacheDir, "${url.hashCode()}.svg")
    private fun bitmapFileFor(url: String) = File(bmpCacheDir, "${url.hashCode()}.png")
    private fun tempFileFor(url: String) = File(tempCacheDir, "${url.hashCode()}.tmp")

    private class PauseException : Exception("Download paused")
}