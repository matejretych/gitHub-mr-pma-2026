package cz.matejretych.coffeeorderv2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cz.matejretych.coffeeorderv2.databinding.ActivitySummaryBinding

class SummaryActivity : ComponentActivity() {
    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        )
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        // finish() odkryje puvodni formular se zachovanym vyberem.
        binding.btnBack.setOnClickListener { finish() }

        val order = readOrder()
        if (order == null) {
            binding.tvSelectedCoffee.setText(R.string.invalid_order)
            binding.tvExtras.visibility = View.GONE
            binding.tvUnitPrice.visibility = View.GONE
            binding.tvFinalPrice.visibility = View.GONE
            return
        }
        val extras = buildList {
            if (order.oatMilk) add(getString(R.string.extra_oat_milk))
            if (order.vanilla) add(getString(R.string.extra_vanilla))
            if (order.extraShot) add(getString(R.string.extra_shot))
        }.joinToString(getString(R.string.list_separator)).ifEmpty { getString(R.string.no_extras) }
        binding.tvSelectedCoffee.text = getString(R.string.selected_coffee, order.quantity, coffeeName(order.coffee))
        binding.tvExtras.text = getString(R.string.selected_extras, extras)
        binding.tvUnitPrice.text = getString(R.string.unit_price, formatPrice(order.unitPrice))
        binding.tvFinalPrice.text = getString(
            R.string.total_price, formatPrice(intent.getIntExtra(OrderExtras.TOTAL_PRICE, 0))
        )
    }

    private fun readOrder(): CoffeeOrder? {
        // String identifikuje model; samostatne boolean extras uchovavaji doplnky.
        val coffeeCode = intent.getStringExtra(OrderExtras.COFFEE)
        val coffee = Coffee.entries.firstOrNull { it.name == coffeeCode } ?: return null
        val quantity = intent.getIntExtra(OrderExtras.QUANTITY, 0)
        if (quantity !in 1..CoffeeOrder.MAX_QUANTITY) return null
        val order = CoffeeOrder(
            coffee = coffee,
            oatMilk = intent.getBooleanExtra(OrderExtras.OAT_MILK, false),
            vanilla = intent.getBooleanExtra(OrderExtras.VANILLA, false),
            extraShot = intent.getBooleanExtra(OrderExtras.EXTRA_SHOT, false),
            quantity = quantity
        )
        // Pri neuplnych datech nezobrazujeme falesnou rekapitulaci s nulovou cenou.
        return order.takeIf { intent.getIntExtra(OrderExtras.TOTAL_PRICE, -1) == it.totalPrice }
    }
}
