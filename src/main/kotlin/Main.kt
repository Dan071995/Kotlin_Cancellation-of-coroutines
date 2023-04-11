import kotlinx.coroutines.*
import java.lang.IllegalStateException
import kotlin.concurrent.timer


fun main() {
    val timer1 = System.currentTimeMillis() //Запоминаем время входа в программу
    val y = Job() //джоб корутины, печатающей точки. Если данной кортуине не присвоить свой job то он будет унаследован из
    //скоупа runBlocking. Так как данная корутина выполняется бесконечно, то и runBlocking будет выполняться бесконечно.
    //Мы отменим данный джоб (и, соответственно саму корутину) далее, после выполнения runBlocking (в данном скоупе идет
    // расчет чисел фибаначи).

    val printResultsList = mutableListOf<String>() //Создадим список, куда будем записывать результаты работы корутин

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, IllegalStateException -> //обработчик ошибок
        printResultsList.add("ERROR from CoroutineExceptionHandler - ${IllegalStateException.message}")
        //Добавляем строку с ошибкой из ексепшенХендлера в список для печати
    }

    val rbJob = runBlocking(exceptionHandler){

       //запускаем корутину, печатающую прогресс
       launch(y + Dispatchers.Default) {
           printProgress()
        }

            //Делаем функцию take() отменяемой
            launch(Job()) {
                cancel() //отменим данную корутину. Если внутри функции take() нет проверки Job-а корутины, то cancel() не сработает
                //и в консоль будет выведено число 5. Чтобы наша корутина была отменяемой в функции take() нужно добавить yield()
                printResultsList.add( Fibonacci.take(-5) )
            }

            //Отменим корутину при привышении определенного времени. Обработаем данное исключение с помощью блока try/catch
            launch {
                try {
                  withTimeout(5) {
                      printResultsList.add( Fibonacci.take(100000000) )
                    }

                } catch (e: IllegalStateException) {
                    printResultsList.add( "Корутина была отменена из-за превышения времени выполнения!" )
                }
            }

            launch(Job()) {
                cancel()
                //println(Fibonacci.take(20))
                printResultsList.add( Fibonacci.take(20) )
            }

            launch { printResultsList.add( Fibonacci.take(220) ) }
            launch { printResultsList.add( Fibonacci.take(200) ) }
            launch { printResultsList.add( Fibonacci.take(-180) ) }

    }

    //Когда Job runBlocking заверщен, печатаем сообщение в консоль, отменяем выполнение корутины, печатающей точки и выводим
    //массив с результатами на экран
    println("\n\nJob from runBlocking is completed? - " + rbJob.isCompleted)
        y.cancel() //отменяем джоб корутины, и соответственно саму корутину, печатающий точки
        Thread.sleep(500) //поставим небольшую задержку, для наглядности отображения информации в консоли
        println("Results:")
        printResultsList.forEach { println(it) } //выводим информацию от корутин на экран
        val timer2 = System.currentTimeMillis() //Запоминаем время выхода из программы
        println("Running time: " + (timer2-timer1) / 1000 + " sec.")

}

suspend fun printProgress(){
    print("Calculating.")
    while (currentCoroutineContext().job.isActive){
        delay(200)
        print(".")
    }
}




