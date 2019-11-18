package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max
import java.util.Stack



// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null

        var parent: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        newNode.parent = closest
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    // быстродействие O(h) h - height of the tree
    // трудоёмкость O(1)
    override fun remove(element: T): Boolean {
        val item = find(element)

        return if (item == null || element.compareTo(item.value) != 0) false
        else {
            size--
            val parent = item.parent

            when {
                item.left == null && item.right == null -> parent.swapElement(item, null)
                item.left == null -> parent.swapElement(item, item.right)
                item.right == null -> parent.swapElement(item, item.left)
                else -> {
                    var swapNode = item.left!!

                    while (swapNode.right != null) {
                        swapNode = swapNode.right!!
                    }

                    swapNode.parent.swapElement(swapNode, swapNode.left)

                    val newNode = Node(swapNode.value)

                    newNode.left = if (item.left?.value == swapNode.value) item.left?.left else item.left
                    newNode.right = item.right

                    parent.swapElement(item, newNode)
                }
            }
            true
        }
    }

    private fun Node<T>?.swapElement(node: Node<T>, newNode: Node<T>?) {
        newNode?.parent = this

        when {
            this == null -> root = newNode
            this.left?.value?.compareTo(node.value) == 0 -> this.left = newNode
            else -> this.right = newNode
        }


    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {

        private var current: Node<T>? = null
        private var stack: Stack<Node<T>> = Stack()

        init {
            var node = root
            while (node != null) {
                stack.push(node)
                node = node.left
            }
        }

        /**
         * Проверка наличия следующего элемента
         * Средняя
         */
        // быстродействие O(1)
        // трудоёмкость O(h) h - height of the tree
        override fun hasNext(): Boolean = stack.isNotEmpty()

        /**
         * Поиск следующего элемента
         * Средняя
         */
        // быстродействие O(n)
        // трудоёмкость O(h) h - height of the tree
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()

            var node = stack.pop()
            current = node
            if (node.right != null) {
                node = node.right

                while (node != null) {
                    stack.push(node)
                    node = node.left
                }
            }
            return current!!.value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        // быстродействие O(h) h - height of the tree
        // трудоёмкость O(1)
        override fun remove() {
            remove(current?.value ?: return)
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    // быстродействие в плохом случае O(n)
    // трудоёмкость в плохом случае O(n)
    // связи с деревом не предусмотрено
    override fun headSet(toElement: T): SortedSet<T> {
        val result = sortedSetOf<T>()
        val stack = Stack<Node<T>>()
        var node = root

        while (node != null) {
            stack.push(node)
            node = node.left
        }

        while (stack.size != 0) {
            node = stack.pop()
            if (node.value < toElement) result.add(node.value)
            if (node?.right != null && node.value < toElement) {
                node = node.right

                while (node != null) {
                    stack.push(node)
                    node = node.left
                }
            }
        }
        return result
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    // быстродействие в плохом случае O(n)
    // трудоёмкость в плохом случае O(n)
    // связи с деревом не предусмотрено
    override fun tailSet(fromElement: T): SortedSet<T> {
        val result = sortedSetOf<T>()
        val stack = Stack<Node<T>>()
        var node = root

        while (node != null) {
            stack.push(node)
            node = node.right
        }

        while (stack.size != 0) {
            node = stack.pop()
            if (node.value >= fromElement) result.add(node.value)
            if (node?.left != null && node.value > fromElement) {
                node = node.left

                while (node != null) {
                    stack.push(node)
                    node = node.right
                }
            }
        }
        return result
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
