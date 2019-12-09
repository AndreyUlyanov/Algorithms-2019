@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    TODO()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
// Быстродействие O(n^2)
// Трудоемкость O(n)
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.isEmpty()) return emptyList()
    if (list.size == 1) return list

    val helpList = Array(list.size) { 0 }
    helpList[0] = 1
    var length = 1
    var index = 0
    for (i in 1 until list.size) {
        var max = 0
        for (j in 0 until i) {
            if (list[j] < list[i] && helpList[j] > max) {
                max = helpList[j]
            }
        }
        if (++max > length) {
            length = max
            index = i
        }
        helpList[i] = max
    }

    val ans = MutableList(length) { 0 }
    ans[length - 1] = list[index]
    if (length == 1) return ans
    for (i in index - 1 downTo 0) {
        if (list[i] < ans[helpList[i]]) {
            ans[helpList[i] - 1] = list[i]
        }
    }

    return ans
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
// Быстродействие O(n^2)
// Трудоемкость O(n)
fun shortestPathOnField(inputName: String): Int {
    var ans = 0
    File(inputName).bufferedReader().use {
        var line = it.readLine()
        var intLine = line.split(" ")

        val list = Array(intLine.size) { 0 }
        val prelist = Array(intLine.size) { 0 }
        list[0] = intLine[0].toInt()
        for (i in 1 until prelist.size) {
            list[i] = intLine[i].toInt() + list[i - 1]
        }
        line = it.readLine()
        while (line != null) {
            for (i in list.indices) {
                prelist[i] = list[i]
            }

            intLine = line.split(" ")
            list[0] = intLine[0].toInt() + prelist[0]

            for (i in 1 until intLine.size) {
                list[i] = intLine[i].toInt() + minOf(list[i - 1], prelist[i - 1], prelist[i])
            }

            line = it.readLine()
        }

        ans = list.last()
    }
    return ans
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5