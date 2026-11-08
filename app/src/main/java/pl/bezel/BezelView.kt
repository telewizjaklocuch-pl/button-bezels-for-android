package pl.bezel

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * Pełnoekranowa, niedotykalna nakładka. Przy prawej krawędzi rysuje ciemne
 * "wgłębienie", które pojawia się i znika po naciśnięciu przycisku.
 */
class BezelView(context: Context) : View(context) {

    companion object {
        const val SLOT_VOLUME_UP = 0
        const val SLOT_VOLUME_DOWN = 1
        const val SLOT_POWER = 2

        // === USTAWIENIA DO DOSTROJENIA POD POCO X3 PRO ===
        // Pozycja pionowa przycisku jako ułamek wysokości ekranu (0 = góra, 1 = dół).
        // Jeśli wgłębienie jest za wysoko/nisko względem prawdziwego przycisku, zmień te liczby.
        private val Y_FRACTION = floatArrayOf(0.255f, 0.325f, 0.435f)

        // Połowa wysokości wgłębienia w dp: głośność dłuższa, zasilanie krótsze.
        private val HALF_HEIGHT_DP = floatArrayOf(26f, 26f, 22f)

        // Maksymalna głębokość wgłębienia w dp.
        private const val DEPTH_DP = 9f

        // Czas animacji w ms.
        private const val DURATION_MS = 260L

        // Kolor wgłębienia (ARGB).
        private const val COLOR = 0xE6000000.toInt()
    }

    private val density = resources.displayMetrics.density
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR
        style = Paint.Style.FILL
    }
    private val path = Path()
    private val depth = FloatArray(3)
    private val animators = arrayOfNulls<ValueAnimator>(3)

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun press(slot: Int) {
        if (slot !in 0..2) return
        animators[slot]?.cancel()
        animators[slot] = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = DURATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                depth[slot] = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        for (slot in 0..2) {
            val d = depth[slot]
            if (d <= 0f) continue
            val cy = h * Y_FRACTION[slot]
            val half = HALF_HEIGHT_DP[slot] * density
            val x = w - d * DEPTH_DP * density * 1.33f
            path.reset()
            path.moveTo(w, cy - half)
            path.cubicTo(x, cy - half * 0.55f, x, cy + half * 0.55f, w, cy + half)
            path.close()
            canvas.drawPath(path, paint)
        }
    }
}
