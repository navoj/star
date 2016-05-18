package org.star_lang.star.operators.spawn.runtime;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;

@SuppressWarnings("serial")
public class Task implements PrettyPrintable, Callable<IValue>
{
  private static final String spawn = "spawn";

  private IValue value;
  private final IFunction program;

  private final static ExecutorService unboundedRunner = Executors.newCachedThreadPool(new DaemonThreads());
  
  private final static int numProcessorsAtStartup = Runtime.getRuntime().availableProcessors();
  private final static int numProcsMultiplier = 1;
  private final static ForkJoinPool boundedRunner = new ForkJoinPool(numProcessorsAtStartup*numProcsMultiplier, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
  
  private final static ScheduledExecutorService scheduledRunner = Executors.newSingleThreadScheduledExecutor(new DaemonThreads());

  private final static ThreadLocal<IValue> currentFiber = new ThreadLocal<>();
  
  public static IValue getCurrentFiber(IFunction createNew) throws EvaluationException
  {
    IValue v = currentFiber.get();
    if (v == null) {
      // will be null for the first calls in the main thread, and in threads outside
      // of the thread pool that executed the fibers.
      
      // If we'll need to differentiate between the main thread and others; then we could pass
      // a flag to createNew, determined either by something set in main(), or by relying
      // on currentThread().NAME = "main"?
      v = createNew.enter();
      currentFiber.set(v);
    }
    return v;
  }
  
  public static void setCurrentFiber(IValue v) {
    currentFiber.set(v);
  }
  
  public Task(IFunction program)
  {
    this.program = program;
  }

  public static void scheduleFuture(FutureTask<IValue> task)
  {
    unboundedRunner.submit(task);
  }

  public static void scheduleFutureQueued(ForkJoinTask<?> task)
  {
    if (ForkJoinTask.inForkJoinPool())
      task.fork();
    else
      boundedRunner.execute(task);
  }
  
  public static void scheduleFutureDelayed(FutureTask<IValue> task, long delayInMS)
  {
    scheduledRunner.schedule(task, delayInMS, TimeUnit.MILLISECONDS);
  }

  @Override
  public IValue call() throws Exception
  {
    try {
      return value = program.enter();
    }
    catch (Exception t) {
      System.out.println("Task failed due to an unhandled exception: " + t.toString());
      t.printStackTrace();
      throw t;
    }
  }

  public IValue getValue()
  {
    return value;
  }

  public void setValue(IValue value)
  {
    this.value = value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(spawn);
    ((PrettyPrintable) program).prettyPrint(disp);
  }

  // Lifted from DefaultThreadFactory, just change to set daemon to true
  public static class DaemonThreads implements ThreadFactory
  {
    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final ThreadGroup group;
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;

    public DaemonThreads()
    {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r)
    {
      Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      t.setDaemon(true);
      t.setPriority(Thread.NORM_PRIORITY);
      return t;
    }
  }
}
