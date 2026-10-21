package cz.matejretych.coffeeorderv2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cz.matejretych.coffeeorderv2.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentOrder = CoffeeOrder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        currentOrder = savedInstanceState?.getBundle(KEY_CURRENT)?.toOrder() ?: CoffeeOrder()
        configureLabels()
        binding.rgCoffee.check(when (currentOrder.coffee) {
            Coffee.ESPRESSO -> R.id.rbEspresso
            Coffee.CAPPUCCINO -> R.id.rbCappuccino
            Coffee.LATTE -> R.id.rbLatte
        })
        binding.cbOatMilk.isChecked = currentOrder.oatMilk
        binding.cbVanilla.isChecked = currentOrder.vanilla
        binding.cbExtraShot.isChecked = currentOrder.extraShot

        // Lambda posluchace okamzite prepocitaji cenu pri kazde zmene.
        binding.rgCoffee.setOnCheckedChangeListener { _, checkedId ->
            currentOrder = currentOrder.copy(coffee = when (checkedId) {
                R.id.rbCappuccino -> Coffee.CAPPUCCINO
                R.id.rbLatte -> Coffee.LATTE
                else -> Coffee.ESPRESSO
            })
            renderConfiguration()
        }
        binding.cbOatMilk.setOnCheckedChangeListener { _, checked ->
            currentOrder = currentOrder.copy(oatMilk = checked)
            renderConfiguration()
        }
        binding.cbVanilla.setOnCheckedChangeListener { _, checked ->
            currentOrder = currentOrder.copy(vanilla = checked)
            renderConfiguration()
        }
        binding.cbExtraShot.setOnCheckedChangeListener { _, checked ->
            currentOrder = currentOrder.copy(extraShot = checked)
            renderConfiguration()
        }
        binding.btnDecrease.setOnClickListener { changeQuantity(-1) }
        binding.btnIncrease.setOnClickListener { changeQuantity(1) }
        binding.btnOrder.setOnClickListener {
            // Explicitni Intent predava hodnoty, nikoliv reference na prvky UI.
            val summaryIntent = Intent(this, SummaryActivity::class.java).apply {
                putExtra(OrderExtras.COFFEE, currentOrder.coffee.name)
                putExtra(OrderExtras.OAT_MILK, currentOrder.oatMilk)
                putExtra(OrderExtras.VANILLA, currentOrder.vanilla)
                putExtra(OrderExtras.EXTRA_SHOT, currentOrder.extraShot)
                putExtra(OrderExtras.QUANTITY, currentOrder.quantity)
                putExtra(OrderExtras.TOTAL_PRICE, currentOrder.totalPrice)
            }
            startActivity(summaryIntent)
        }
        renderConfiguration()
    }

    private fun configureLabels() {
        binding.rbEspresso.text = coffeeLabel(Coffee.ESPRESSO)
        binding.rbCappuccino.text = coffeeLabel(Coffee.CAPPUCCINO)
        binding.rbLatte.text = coffeeLabel(Coffee.LATTE)
        binding.cbOatMilk.text = extraLabel(R.string.extra_oat_milk, CoffeeOrder.OAT_MILK_PRICE)
        binding.cbVanilla.text = extraLabel(R.string.extra_vanilla, CoffeeOrder.VANILLA_PRICE)
        binding.cbExtraShot.text = extraLabel(R.string.extra_shot, CoffeeOrder.EXTRA_SHOT_PRICE)
    }

    private fun coffeeLabel(coffee: Coffee): String =
        getString(R.string.choice_price, coffeeName(coffee), formatPrice(coffee.priceCzk))

    private fun extraLabel(nameId: Int, price: Int): String =
        getString(R.string.extra_price, getString(nameId), formatPrice(price))

    private fun changeQuantity(change: Int) {
        currentOrder = currentOrder.copy(
            quantity = (currentOrder.quantity + change).coerceIn(1, CoffeeOrder.MAX_QUANTITY)
        )
        renderConfiguration()
    }

    private fun renderConfiguration() {
        binding.ivCoffee.setImageResource(when (currentOrder.coffee) {
            Coffee.ESPRESSO -> R.drawable.img_coffee_espresso
            Coffee.CAPPUCCINO -> R.drawable.img_coffee_cappuccino
            Coffee.LATTE -> R.drawable.img_coffee_latte
        })
        binding.ivCoffee.contentDescription = getString(R.string.coffee_image, coffeeName(currentOrder.coffee))
        binding.tvQuantity.text = getString(R.string.quantity_value, currentOrder.quantity)
        binding.tvQuantity.contentDescription = getString(R.string.quantity_description, currentOrder.quantity)
        binding.btnDecrease.isEnabled = currentOrder.quantity > 1
        binding.btnIncrease.isEnabled = currentOrder.quantity < CoffeeOrder.MAX_QUANTITY
        binding.btnDecrease.alpha = if (binding.btnDecrease.isEnabled) 1f else 0.3f
        binding.btnIncrease.alpha = if (binding.btnIncrease.isEnabled) 1f else 0.3f
        binding.tvUnitPrice.text = getString(R.string.unit_price, formatPrice(currentOrder.unitPrice))
        binding.tvTotalPrice.text = getString(R.string.total_price, formatPrice(currentOrder.totalPrice))
    }

    // Zachovani formulare pri otoceni nebo obnove Activity ze zadniho zasobniku.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(KEY_CURRENT, Bundle().apply {
            putString(OrderExtras.COFFEE, currentOrder.coffee.name)
            putBoolean(OrderExtras.OAT_MILK, currentOrder.oatMilk)
            putBoolean(OrderExtras.VANILLA, currentOrder.vanilla)
            putBoolean(OrderExtras.EXTRA_SHOT, currentOrder.extraShot)
            putInt(OrderExtras.QUANTITY, currentOrder.quantity)
        })
        super.onSaveInstanceState(outState)
    }

    private fun Bundle.toOrder() = CoffeeOrder(
        coffee = Coffee.entries.firstOrNull { it.name == getString(OrderExtras.COFFEE) } ?: Coffee.ESPRESSO,
        oatMilk = getBoolean(OrderExtras.OAT_MILK),
        vanilla = getBoolean(OrderExtras.VANILLA),
        extraShot = getBoolean(OrderExtras.EXTRA_SHOT),
        quantity = getInt(OrderExtras.QUANTITY, 1).coerceIn(1, CoffeeOrder.MAX_QUANTITY)
    )

    companion object {
        private const val KEY_CURRENT = "current_order"
    }
}
