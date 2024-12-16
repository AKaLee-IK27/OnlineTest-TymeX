class Product(val name: String, val price: Double, val quantity: Int) {
    init {
        require(price >= 0) { "Price must be non-negative" }
        require(quantity >= 0) { "Quantity must be non-negative" }
    }

    override fun toString(): String {
        return "Product(name='$name', price=$price, quantity=$quantity)"
    }
}

fun totalInventoryValue(inventory: List<Product>): Double {
    return inventory.sumOf { it.price * it.quantity }
}

fun theMostExpensiveProduct(inventory: List<Product>): String {
    val product = inventory.maxByOrNull { it.price }
    return product?.name ?: "No products in inventory"
}

fun isProductInInventory(inventory: List<Product>, productName: String): Boolean {
    return inventory.any { it.name.equals(productName, ignoreCase = true) }
}

fun sortInventory(
    inventory: List<Product>,
    isDescending: Boolean = true,
    sortByQuantity: Boolean = false
): List<Product> {
    return if (isDescending) {
        if (sortByQuantity) inventory.sortedByDescending { it.quantity }
        else inventory.sortedByDescending { it.price }
    } else {
        if (sortByQuantity) inventory.sortedBy { it.quantity }
        else inventory.sortedBy { it.price }
    }
}

fun main() {
    val inventory = listOf(
        Product("Laptop", 999.99, 5),
        Product("Smartphone", 499.99, 10),
        Product("Tablet", 299.99, 7),
        Product("Smartwatch", 199.99, 3)
    )

    // Task 1: Total Inventory Value
    println("Total Inventory Value: %.2f".format(totalInventoryValue(inventory)))

    // Task 2: The Most Expensive Product
    println("The Most Expensive Product: ${theMostExpensiveProduct(inventory)}")

    // Task 3: Check if a Product is in Inventory
    val productName = "Headphones"
    println("$productName is in inventory: ${isProductInInventory(inventory, productName)}")

    // Task 4: Sort Inventory
    println("\nInventory Sorted by Price Descending:")
    sortInventory(inventory, isDescending = true, sortByQuantity = false).forEach { println(it) }

    println("\nInventory Sorted by Quantity Ascending:")
    sortInventory(inventory, isDescending = false, sortByQuantity = true).forEach { println(it) }
}