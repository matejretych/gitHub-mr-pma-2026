package cz.matejretych.coffeeorderv2

import org.junit.Assert.assertEquals
import org.junit.Test

class CoffeeOrderTest {
    @Test fun basePricesMatchMenu() {
        assertEquals(55, CoffeeOrder(Coffee.ESPRESSO).totalPrice)
        assertEquals(75, CoffeeOrder(Coffee.CAPPUCCINO).totalPrice)
        assertEquals(85, CoffeeOrder(Coffee.LATTE).totalPrice)
    }

    @Test fun extrasApplyToEveryDrink() {
        val order = CoffeeOrder(Coffee.LATTE, oatMilk = true, vanilla = true, extraShot = true, quantity = 3)
        assertEquals(130, order.unitPrice)
        assertEquals(390, order.totalPrice)
    }

    @Test fun changingConfigurationDoesNotChangeSubmittedOrder() {
        val submittedOrder = CoffeeOrder(Coffee.CAPPUCCINO, oatMilk = true, quantity = 2)
        val editedOrder = submittedOrder.copy(oatMilk = false, quantity = 1)
        assertEquals(180, submittedOrder.totalPrice)
        assertEquals(75, editedOrder.totalPrice)
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroQuantityIsRejected() { CoffeeOrder(quantity = 0) }

    @Test(expected = IllegalArgumentException::class)
    fun quantityAboveLimitIsRejected() { CoffeeOrder(quantity = 11) }
}
