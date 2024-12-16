fun findMissingNumber(arr: List<Int>): Int {
    val n = arr.size + 1
    val expectedSum = n * (n + 1) / 2

    val actualSum = arr.sum()

    return expectedSum - actualSum
}

fun main() {
    val inputArray = listOf(3, 7, 1, 2, 6, 4)
    val missingNumber = findMissingNumber(inputArray)

    println("The missing number is: $missingNumber")
}
