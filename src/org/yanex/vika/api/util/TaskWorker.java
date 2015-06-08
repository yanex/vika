package org.yanex.vika.api.util;

import java.util.Vector;

public class TaskWorker implements Runnable {

    private final Vector queue = new Vector();

    public TaskWorker() {
        new Thread(this).start();
    }

    public TaskWorker(int priority) {
        Thread t = new Thread(this);
        t.setPriority(priority);
        t.start();
    }

    public void addTask(Runnable task) {
        synchronized (queue) {
            queue.addElement(task);
            queue.notify();
        }
    }

    public int getPendingCount() {
        return queue.size();
    }

    public void run() {
        while (true) {
            Runnable task = getNext();
            if (task != null) {
                task.run();
                queue.removeElement(task);
            } else synchronized (queue) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Runnable getNext() {
        Runnable task = null;
        if (!queue.isEmpty()) {
            task = (Runnable) queue.firstElement();
        }
        return task;
    }
}
