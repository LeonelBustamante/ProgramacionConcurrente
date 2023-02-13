package lectorEscritor;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LectorEscritor {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        new Thread(new Lector(buffer), "LECTOR-1").start();
        new Thread(new Lector(buffer), "LECTOR-2").start();
        new Thread(new Escritor(buffer), "ESCRITOR-1").start();
        new Thread(new Escritor(buffer), "ESCRITOR-2").start();
    }
}

class Buffer {
    private ArrayList<Integer> buffer = new ArrayList<Integer>();
    private boolean disponible = false;
    private Lock lock = new ReentrantLock();
    private Condition leyendo = lock.newCondition();
    private Condition escribiendo = lock.newCondition();

    public void escribir(int valor) {
        lock.lock();
        try {
            while (disponible) {
                try {
                    leyendo.await();
                } catch (InterruptedException e) {
                }
            }
            buffer.add(valor);
            disponible = true;
            escribiendo.signal();
        } finally {
            lock.unlock();
        }
    }

    public int leer() {
        lock.lock();
        try {
            while (!disponible) {
                try {
                    escribiendo.await();
                } catch (InterruptedException e) {
                }
            }
            disponible = false;
            leyendo.signal();
        } finally {
            lock.unlock();
        }
        return buffer.remove(0);
    }
}

class Lector implements Runnable {
    private Buffer buffer;

    public Lector(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            int valor = buffer.leer();
            System.out.println(Thread.currentThread().getName() + " leyó: " + valor);
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
            }
        }
    }
}

class Escritor implements Runnable {
    private Buffer buffer;
    private static int contador = 0;

    public Escritor(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            int valor = ++contador;
            buffer.escribir(valor);
            System.out.println(Thread.currentThread().getName() + " escribió: " + valor);
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
            }
        }
    }
}
