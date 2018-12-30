package ru.bvpotapenko.se.threads;

/**
 * Hello world!
 */
public class App {
    @FunctionalInterface // 1. Создайём промежуточный интерфейс, метод которого будет явного бросать исключение
    public interface ThrowingRunnable {
        void runWitException() throws Exception; //Здесь мы явно бросаем исключение
    }
    //2. Метод, который на входе принимает объект с функциональным интерфейсом и возвращает Runnable
    private static Runnable lambdaWrapper(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.runWitException();
            } catch (Exception anyException) {
                throw new RuntimeException(anyException.getMessage());
            }
        };
    }
    public static void main(String[] args) {
        System.out.println("Start");
        //new Thread(() -> method5("CHARLIE")).start(); //Так не работает
        //Лямбда - это синтаксический сахар для описания анонимного класа. У нас она описывает имплементацию ThrowingRunnable.runWitException()
        new Thread(lambdaWrapper(() -> method5("ALPHA"))).start();//3. оборачиваем лямбду"()->{}" в  lambdaWrapper(...)
        new Thread(lambdaWrapper(() -> method5("BRAVO"))).start();
    }
    //Этот метод мы вызываем в лямбде () -> method5(String)
    private synchronized static void method5(String threadName) throws InterruptedException {
        System.out.println("M5-" + threadName + "-begin");
        for (int i = 0; i < 3; i++) {
            System.out.println("Thread_" + threadName + ": " + i);
            Thread.sleep(100);
        }
        System.out.println("M5-" + threadName + "-ends");
    }

    static void testRunnable() {
        new Thread(() -> method4("ALPHA")).start();
        new Thread(() -> method4("BRAVO")).start();
    }

    synchronized void method1() throws InterruptedException {
        System.out.println("M1-begin");
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            Thread.sleep(100);
        }
        System.out.println("M1-end");
    }

    synchronized void method2() throws InterruptedException {
        System.out.println("M2-begin");
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            Thread.sleep(100);
        }
        System.out.println("M2-end");
    }

    private Object lock = new Object();

    private void method3(String name) {
        System.out.println("M3-begin");
        for (int i = 0; i < 3; i++) {
            System.out.println("Thread_" + name + ": " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synchronized (lock) {
            System.out.println("Synchronized-" + name + "-begin");
            for (int i = 0; i < 3; i++) {
                System.out.println("Thread_" + name + ": " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Synchronized-" + name + "-end");
        }
        System.out.println("M3-end");
    }

    private synchronized static void method4(String name) {
        System.out.println("M4-" + name + "-begin");
        for (int i = 0; i < 3; i++) {
            System.out.println("Thread_" + name + ": " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("M4-" + name + "-end");
    }
}
