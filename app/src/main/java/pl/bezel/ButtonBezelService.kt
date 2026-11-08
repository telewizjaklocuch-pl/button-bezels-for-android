package pl.bezel

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.view.KeyEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

/**
 * Usługa dostępności: wykrywa przyciski głośności (zdarzenia klawiszy) oraz
 * włączenie ekranu (przybliżenie przycisku zasilania) i uruchamia animację.
 *
 * Przycisku zasilania Android nie udostępnia aplikacjom, więc animacja dla niego
 * pojawia się dopiero po zapaleniu ekranu (ACTION_SCREEN_ON).
 */
class ButtonBezelService : AccessibilityService() {

    private var windowManager: WindowManager? = null
    private var bezelView: BezelView? = null

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                bezelView?.press(BezelView.SLOT_POWER)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val view = BezelView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
        windowManager?.addView(view, params)
        bezelView = view

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(screenReceiver, filter)
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> bezelView?.press(BezelView.SLOT_VOLUME_UP)
                KeyEvent.KEYCODE_VOLUME_DOWN -> bezelView?.press(BezelView.SLOT_VOLUME_DOWN)
            }
        }
        // false = nie przechwytuj; system dalej obsługuje głośność normalnie.
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        try {
            unregisterReceiver(screenReceiver)
        } catch (_: IllegalArgumentException) {
        }
        bezelView?.let { windowManager?.removeView(it) }
        bezelView = null
        super.onDestroy()
    }
}
