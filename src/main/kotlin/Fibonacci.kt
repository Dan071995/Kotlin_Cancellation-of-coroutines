import kotlinx.coroutines.*
import java.math.BigInteger
import kotlin.math.absoluteValue

object Fibonacci {

    suspend fun take(x:Int): String {

        //yield() //сделаем данную функцию отменяемой. Проверка в данном месте не сработает только при запуске функции
        //чтобы корутина была отменяемой на всей стадии ее жизни, добавим функцию yield() в цикл расчета числа Фабаначи
        if (!currentCoroutineContext().isActive) error("Корутина была отменена!") //Аналог yield() только мы возбуждаем ошибку, а не исключение. Без обработчика ошибок данный код остановит всю программу
                                               //throw CancellationException() //полный аналог yield(), и обработчик ошибок не нужен

        // если в функцию передают отрицательное число берем его по модулю. Так же, если мы хотим узнать 1-ый или 2-й член последовательности
        // то рассчитывать их не нужно
        var number = x
        if (number == 0 || number == 1) return "For number = $number Fibonathhi Value is: 1"
        if (number <= 0) number = x.absoluteValue
        //считаем интересующий нас член последовательности и возвращаем результат
        return "For number = $number Fibonathhi Value is: ${calculateVal(number)}"
    }

}

//Расчет числа Фибаначчи
private suspend fun calculateVal(n: Int):BigInteger{

    var fib1 = 1.toBigInteger()
    var fib2 = 1.toBigInteger()
    var fibSum = 0.toBigInteger()
    var i = 0

    while (i < n - 2 ){
        yield() //проверяем что корутина не была отменена.
        fibSum = fib1 + fib2
        fib1 = fib2
        fib2 = fibSum
        i++
    }
    return fibSum

}