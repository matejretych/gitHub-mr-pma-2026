package cz.matejretych.coffeeorderv2

import android.content.Context
import java.text.NumberFormat
import java.util.Currency

// Obrazovky pouzivaji stejne nazvy a stejny format cen ve vsech jazycich.
fun Context.coffeeName(coffee: Coffee): String = getString(when (coffee) {
    Coffee.ESPRESSO -> R.string.coffee_espresso
    Coffee.CAPPUCCINO -> R.string.coffee_cappuccino
    Coffee.LATTE -> R.string.coffee_latte
})

fun Context.formatPrice(price: Int): String =
    NumberFormat.getCurrencyInstance(resources.configuration.locales[0]).apply {
        currency = Currency.getInstance("CZK")
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }.format(price)
