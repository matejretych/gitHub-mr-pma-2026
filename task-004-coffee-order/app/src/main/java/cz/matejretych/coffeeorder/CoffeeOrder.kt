package cz.matejretych.coffeeorder

enum class Coffee(val priceCzk: Int) {
    ESPRESSO(55), CAPPUCCINO(75), LATTE(85)
}

// Ceny uchovavame v celych korunach, aby vypocet nepotreboval zaokrouhlovani.
data class CoffeeOrder(
    val coffee: Coffee = Coffee.ESPRESSO,
    val oatMilk: Boolean = false,
    val vanilla: Boolean = false,
    val extraShot: Boolean = false,
    val quantity: Int = 1
) {
    init { require(quantity in 1..MAX_QUANTITY) }

    val unitPrice: Int
        get() = coffee.priceCzk +
            (if (oatMilk) OAT_MILK_PRICE else 0) +
            (if (vanilla) VANILLA_PRICE else 0) +
            (if (extraShot) EXTRA_SHOT_PRICE else 0)

    val totalPrice: Int
        get() = unitPrice * quantity

    companion object {
        const val MAX_QUANTITY = 10
        const val OAT_MILK_PRICE = 15
        const val VANILLA_PRICE = 10
        const val EXTRA_SHOT_PRICE = 20
    }
}
