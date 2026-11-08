package pl.bezel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (24 * resources.displayMetrics.density).toInt()

        val text = TextView(this).apply {
            textSize = 16f
            text = "Bezel Buttons\n\n" +
                "1. Naciśnij przycisk poniżej.\n" +
                "2. Znajdź \"Bezel Buttons\" w usługach dostępności (może być w \"Zainstalowane aplikacje\") i włącz.\n" +
                "3. Naciśnij przycisk głośności. Przy prawej krawędzi pojawi się wgłębienie.\n\n" +
                "Na MIUI/HyperOS: w ustawieniach aplikacji wyłącz ograniczenia baterii " +
                "(Bez ograniczeń) i włącz autostart, inaczej system może ubić usługę."
        }
        val button = Button(this).apply {
            this.text = "Otwórz ustawienia dostępności"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(pad, pad, pad, pad)
            addView(text)
            addView(button)
        })
    }
}
