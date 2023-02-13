package barberoDormilon;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class BarberoDormilon {
    public static void main(String[] args) throws InterruptedException {
        Barberia barberia = new Barberia();
        new Thread(new Barbero(barberia), "BARBERO").start();

        for (int i = 0; i < 10; i++) {
            new Thread(new Cliente(barberia), "CLIENTE-" + i).start();
            Thread.sleep(new Random().nextInt(1000));
        }
    }
}

class Barberia {
    Semaphore barbero;
    Semaphore cliente;
    Semaphore accesoSilla;
    Semaphore salaEspera;

    public Barberia() {
        barbero = new Semaphore(0); // El barbero empieza dormido
        cliente = new Semaphore(0); // El cliente empieza esperando
        accesoSilla = new Semaphore(1); // El acceso a la silla es exclusivo
        salaEspera = new Semaphore(3); // La sala de espera tiene 3 sillas

    }

    public void cortarPelo() throws InterruptedException {
        barbero.acquire(); // El barbero espera a que llegue un cliente
        System.out.println("\t" + Thread.currentThread().getName() + " empieza a cortar el pelo");
        Thread.sleep(1000);
        System.out.println("\t " + Thread.currentThread().getName() + " termina de cortar el pelo");
        cliente.release(); // El barbero termina de cortar el pelo y despierta al cliente
    }

    public void esperarTurno() throws InterruptedException {
        if (salaEspera.tryAcquire()) { // El cliente se sienta en la sala de espera
            System.out.println(Thread.currentThread().getName() + " se sienta en la sala de espera");
            accesoSilla.acquire(); // Aca se bloquea si el barbero esta cortando el pelo
            salaEspera.release(); // El cliente se levanta de la silla y libera el acceso a la sala de espera
            System.out.println(Thread.currentThread().getName() + " se sienta en la silla del barbero");
            barbero.release(); // Despierta al barbero
            cliente.acquire(); // El cliente espera a que el barbero termine de cortar el pelo
            accesoSilla.release(); // El cliente se va y libera el acceso a la silla
            System.out.println(Thread.currentThread().getName() + " se fue con el pelo cortado");
        } else {
            System.out.println(Thread.currentThread().getName() + " se fue porque no había lugar en la sala de espera");
        }
    }
}

class Barbero implements Runnable {
    private Barberia barberia;

    public Barbero(Barberia barberia) {
        this.barberia = barberia;
    }

    public void run() {
        try {
            while (true) {
                barberia.cortarPelo();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Cliente implements Runnable {
    private Barberia barberia;

    public Cliente(Barberia barberia) {
        this.barberia = barberia;
    }

    public void run() {
        try {
            barberia.esperarTurno();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
