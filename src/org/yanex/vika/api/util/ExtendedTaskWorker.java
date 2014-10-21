package org.yanex.vika.api.util;

import java.util.Random;

public class ExtendedTaskWorker {

  public static final int TYPE_RANDOM = 0;
  public static final int TYPE_MINPENDING = 1;

  private final TaskWorker workers[];
  private final int type;
  private final Random random;

  public ExtendedTaskWorker(int threadCount, int priority, int type) {
    workers = new TaskWorker[threadCount];
    this.type = type;

    if (type == ExtendedTaskWorker.TYPE_RANDOM) {
      random = new Random();
    } else {
      random = null;
    }

    for (int i = 0; i < workers.length; ++i) {
      workers[i] = new TaskWorker(priority);
    }
  }

  public void addTask(Runnable task) {
    if (type == 0) {
      workers[random.nextInt(workers.length)].addTask(task);
    } else {
      int min = Integer.MAX_VALUE;
      for (int i = 0; i < workers.length; ++i) {
        min = Math.min(min, workers[i].getPendingCount());
      }
      workers[min].addTask(task);
    }
  }

}
