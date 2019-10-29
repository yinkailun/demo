
## 1.四种创建方式(额外加工作窃取线程池)

* Executors.newCachedThreadPool();
  //创建一个缓存线程池，可以无限创建线程池，可以动态回收空闲线程，如果没有，则新建
   ```
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
   ```
* Executors.newFixedThreadPool(1);
  //创建一个有coreSize和maxSize的线程池
  ```
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
  ```
* Executors.newScheduledThreadPool(1);
  //使用延迟队列实现
  ```
    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue());
    }
  ```
* Executors.newSingleThreadExecutor();
  ```
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
  
  ```
* Executors.newWorkStealingPool();
  //使用forkjoinpool
  ```
        return new ForkJoinPool
            (Runtime.getRuntime().availableProcessors(),
             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
             null, true);
  ```
