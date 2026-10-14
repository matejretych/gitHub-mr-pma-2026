package cz.matejretych.coffeeorder

import android.os.Bundle
import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cz.matejretych.coffeeorder.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Currency

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentOrder = CoffeeOrder()
    private var submittedOrder: CoffeeOrder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        )
        // Vsechny prvky layoutu jsou dostupne pres vygenerovany View Binding.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        currentOrder = savedInstanceState?.getBundle(KEY_CURRENT)?.toOrder() ?: CoffeeOrder()
        submittedOrder = savedInstanceState?.getBundle(KEY_SUBMITTED)?.toOrder()
        configureLabels()
        binding.rgCoffee.check(when (currentOrder.coffee) {
            Coffee.ESPRESSO -> R.id.rbEspresso
            Coffee.CAPPUCCINO -> R.id.rbCappuccino
            Coffee.LATTE -> R.id.rbLatte
        })
        binding.cbOatMilk.isChecked = currentOrder.oatMilk
        binding.cbVanilla.isChecked = currentOrder.vanilla
        binding.cbExtraShot.isChecked = currentOrder.extraShot

        binding.rgCoffee.setOnCheckedChangeListener { _, checkedId ->
            val coffee = when (checkedId) {
                R.id.rbCappuccino -> Coffee.CAPPUCCINO
                R.id.rbLatte -> Coffee.LATTE
                else -> Coffee.ESPRESSO
            }
            currentOrder = currentOrder.copy(coffee = coffee)
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
            // Nemenny snimek objednavky se meni pouze stiskem tlacitka Objednat.
            submittedOrder = currentOrder.copy()
            renderSummary()
            binding.svOrder.post { binding.svOrder.fullScroll(View.FOCUS_DOWN) }
        }
        renderConfiguration()
        renderSummary()
    }

    private fun configureLabels() {
        binding.rbEspresso.text = coffeeLabel(Coffee.ESPRESSO)
        binding.rbCappuccino.text = coffeeLabel(Coffee.CAPPUCCINO)
        binding.rbLatte.text = coffeeLabel(Coffee.LATTE)
        binding.cbOatMilk.text = extraLabel(R.string.extra_oat_milk, CoffeeOrder.OAT_MILK_PRICE)
        binding.cbVanilla.text = extraLabel(R.string.extra_vanilla, CoffeeOrder.VANILLA_PRICE)
        binding.cbExtraShot.text = extraLabel(R.string.extra_shot, CoffeeOrder.EXTRA_SHOT_PRICE)
    }

    private fun coffeeName(coffee: Coffee): String = getString(when (coffee) {
        Coffee.ESPRESSO -> R.string.coffee_espresso
        Coffee.CAPPUCCINO -> R.string.coffee_cappuccino
        Coffee.LATTE -> R.string.coffee_latte
    })

    private fun coffeeLabel(coffee: Coffee): String =
        getString(R.string.choice_price, coffeeName(coffee), formatPrice(coffee.priceCzk))

    private fun extraLabel(nameId: Int, price: Int): String =
        getString(R.string.extra_price, getString(nameId), formatPrice(price))

    // Jazyk vybere Android; mena zustava CZK i v anglicke a nemecke verzi.
    private fun formatPrice(price: Int): String =
        NumberFormat.getCurrencyInstance(resources.configuration.locales[0]).apply {
            currency = Currency.getInstance("CZK")
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }.format(price)

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

    private fun renderSummary() {
        val order = submittedOrder
        if (order == null) {
            binding.tvSummary.setText(R.string.empty_summary)
            return
        }
        val extras = buildList {
            if (order.oatMilk) add(getString(R.string.extra_oat_milk))
            if (order.vanilla) add(getString(R.string.extra_vanilla))
            if (order.extraShot) add(getString(R.string.extra_shot))
        }.joinToString(getString(R.string.list_separator)).ifEmpty { getString(R.string.no_extras) }
        binding.tvSummary.text = getString(
            R.string.summary_format, order.quantity, coffeeName(order.coffee),
            extras, formatPrice(order.unitPrice), formatPrice(order.totalPrice)
        )
    }

    // Ukladame data, nikoliv prelozeny text: souhrn se prelozi i po zmene jazyka.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle(KEY_CURRENT, currentOrder.toBundle())
        submittedOrder?.let { outState.putBundle(KEY_SUBMITTED, it.toBundle()) }
        super.onSaveInstanceState(outState)
    }

    private fun CoffeeOrder.toBundle() = Bundle().apply {
        putInt(KEY_COFFEE, coffee.ordinal)
        putBoolean(KEY_OAT, oatMilk)
        putBoolean(KEY_VANILLA, vanilla)
        putBoolean(KEY_SHOT, extraShot)
        putInt(KEY_QUANTITY, quantity)
    }

    private fun Bundle.toOrder() = CoffeeOrder(
        coffee = Coffee.entries[getInt(KEY_COFFEE)],
        oatMilk = getBoolean(KEY_OAT),
        vanilla = getBoolean(KEY_VANILLA),
        extraShot = getBoolean(KEY_SHOT),
        quantity = getInt(KEY_QUANTITY, 1)
    )

    companion object {
        private const val KEY_CURRENT = "current_order"
        private const val KEY_SUBMITTED = "submitted_order"
        private const val KEY_COFFEE = "coffee"
        private const val KEY_OAT = "oat_milk"
        private const val KEY_VANILLA = "vanilla"
        private const val KEY_SHOT = "extra_shot"
        private const val KEY_QUANTITY = "quantity"
    }
}
