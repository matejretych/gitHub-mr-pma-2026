package cz.matejretych.dicecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme { DiceScreen() } }
    }
}

@Composable
private fun DiceScreen() {
    val diceSymbols = remember { listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅") }
    // Compose sleduje stav a pri jeho zmene znovu vykresli zavisle prvky.
    var diceValue by rememberSaveable { mutableIntStateOf(1) }
    var rollCount by rememberSaveable { mutableIntStateOf(0) }
    var lastResult by rememberSaveable { mutableIntStateOf(0) }
    var isRolling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF4F7F7)) {
        // Insets udrzuji obsah mimo stavovou a navigacni listu.
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hoď kostkou", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF202426))
            Box(Modifier.size(220.dp), contentAlignment = Alignment.Center) {
                Text(
                    diceSymbols[diceValue - 1],
                    fontSize = 160.sp,
                    color = Color(0xFF087F72),
                    modifier = Modifier.semantics { contentDescription = "Kostka: $diceValue" }
                )
            }
            Button(
                modifier = Modifier.width(160.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F72)),
                enabled = !isRolling,
                onClick = {
                    isRolling = true
                    // Deset mezihodu po 250 ms a samostatny konecny vysledek.
                    scope.launch {
                        try {
                            repeat(10) {
                                diceValue = Random.nextInt(1, 7)
                                delay(250)
                            }
                            diceValue = Random.nextInt(1, 7)
                            lastResult = diceValue
                            rollCount++
                        } finally {
                            isRolling = false
                        }
                    }
                }
            ) { Text(if (isRolling) "Hází se…" else "Hodit", fontSize = 18.sp) }
            Spacer(Modifier.height(24.dp))
            Text(
                "Počet hodů: $rollCount · Poslední hod: ${if (lastResult == 0) "-" else lastResult.toString()}",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                color = Color(0xFF50585A)
            )
        }
    }
}
