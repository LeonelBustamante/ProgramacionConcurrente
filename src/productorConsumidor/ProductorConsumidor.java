package productorConsumidor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ProductorConsumidor {
    public static void main(String[] args) {
        CintaTransportadora cinta = new CintaTransportadora();
        new Thread((new Productor(cinta)), "PRODUCTOR").start();
        new Thread((new Consumidor(cinta)), "CONSUMIDOR").start();
    }
}

class CintaTransportadora {
    // Con comentarios es Condition, sin comentarios es monitor
    List<Integer> buffer;

    public CintaTransportadora() {
        buffer = new ArrayList<Integer>();
        // lock = new ReentrantLock();
        // vacio = lock.newCondition();
        // lleno = lock.newCondition();
    }

    public synchronized void producir(int codigoBarras) throws InterruptedException {
        // Sin sinchronized
        // lock.lock();
        while (buffer.size() == 10) {
            System.out.println("Buffer lleno, productor espera");
            wait();
            // lleno.await();
        }
        buffer.add(codigoBarras);
        System.out.println("Productor produjo el item " + codigoBarras);
        notify();
        // vacio.signal();
        // finally {
        // lock.unlock();
        // }
    }

    public synchronized void consumir() throws InterruptedException {
        // Sin sinchronized
        // lock.lock();
        while (buffer.size() == 0) {
            System.out.println("Buffer vacio, consumidor espera");
            wait();
            // vacio.await();
        }
        int item = buffer.remove(0);
        System.out.println("Consumidor consumió el item " + item);
        notify();
        // lleno.signal();
        // finally {
        // lock.unlock();
        // }
    }
}

class Productor implements Runnable {
    CintaTransportadora cinta;

    public Productor(CintaTransportadora cinta) {
        this.cinta = cinta;
    }

    public void run() {
        int codigoBarras = 0;
        while (true) {
            try {
                cinta.producir(++codigoBarras);
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
            }
        }
    }
}

class Consumidor implements Runnable {

    CintaTransportadora cinta;

    public Consumidor(CintaTransportadora cinta) {
        this.cinta = cinta;
    }

    public void run() {
        while (true) {
            try {
                cinta.consumir();
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
            }
        }
    }
}
