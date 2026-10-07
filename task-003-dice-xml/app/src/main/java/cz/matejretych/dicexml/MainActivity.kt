package cz.matejretych.dicexml

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")
    private var diceValue = 1
    private var rollCount = 0
    private var lastResult = 0
    private lateinit var diceText: TextView
    private lateinit var summaryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // XML prvky vyhledame primo pomoci jejich ID, bez View Bindingu.
        diceText = findViewById(R.id.tvDice)
        summaryText = findViewById(R.id.tvSummary)
        val rollButton = findViewById<Button>(R.id.btnRoll)
        diceValue = savedInstanceState?.getInt("diceValue", 1) ?: 1
        rollCount = savedInstanceState?.getInt("rollCount", 0) ?: 0
        lastResult = savedInstanceState?.getInt("lastResult", 0) ?: 0
        updateDice()
        updateSummary()

        // Systemove listy nesmi zakryt vystredeny obsah.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llMain)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        rollButton.setOnClickListener {
            rollButton.isEnabled = false
            rollButton.setText(R.string.rolling)
            // Korutina neblokuje UI a pri zniceni Activity se automaticky zrusi.
            lifecycleScope.launch {
                repeat(10) {
                    diceValue = Random.nextInt(1, 7)
                    updateDice()
                    delay(250)
                }
                diceValue = Random.nextInt(1, 7)
                lastResult = diceValue
                rollCount++
                updateDice()
                updateSummary()
                rollButton.isEnabled = true
                rollButton.setText(R.string.roll)
            }
        }
    }

    private fun updateDice() {
        diceText.text = diceSymbols[diceValue - 1]
        diceText.contentDescription = getString(R.string.dice_description, diceValue)
    }

    private fun updateSummary() {
        summaryText.text = getString(R.string.summary, rollCount, if (lastResult == 0) "-" else lastResult.toString())
    }

    // Dokoncene hody a aktualni symbol preziji otoceni zarizeni.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("diceValue", diceValue)
        outState.putInt("rollCount", rollCount)
        outState.putInt("lastResult", lastResult)
        super.onSaveInstanceState(outState)
    }
}
