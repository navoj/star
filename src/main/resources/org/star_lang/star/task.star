/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
private import base;
private import compute;
private import sequences;
private import strings;

-- private
type taskEvalState of %a is
  TaskDone(%a)
  or TaskContinue(task of %a)
  or TaskWait((action(task of %b)) => taskWaitResult of %b, (task of %b) => task of %a)
  or TaskFailure(exception)

-- Type of asynchronous tasks (opaque)
type task of %a is _task(() => taskEvalState of %a)

-- Returns an asynchronous task, that will yield the given value when executed
taskReturn has type (%a) => task of %a
taskReturn(v) is _task((function () is TaskDone(v)))

-- Failure
taskFail has type (exception) => task of %a
taskFail(e) is
  _task((function () is TaskFailure(e)))
  
-- Handle failure
taskCatch has type (task of %a, (exception) => task of %a) => task of %a
taskCatch(_task(b),EF) is let{
   catchTask() is
    case b() in {
      TaskFailure(E) is TaskContinue(EF(E));
      TaskContinue(b2) is TaskContinue(taskCatch(b2, EF));
      TaskWait(start, more) is TaskWait(start, (function (c) is taskCatch(more(c), EF)))
      Other default is Other; -- TaskDone
    }
} in _task(catchTask)

-- Returns an asynchronous task, that, when executed, will 'continue' in the given
-- function as soon as the first task completes.
taskBind has type (task of %b, (%b) => task of %a) => task of %a
taskBind(_task(b), f) is let {
  boundTask() is
    case b() in {
      TaskDone(v) is TaskContinue(f(v)); -- TODO? catch java exceptions in f?
      TaskContinue(b2) is TaskContinue(taskBind(b2, f));
      TaskWait(start, more) is TaskWait(start, (function (c) is taskBind(more(c), f)))
      TaskFailure(e) is TaskFailure(e)
    }
} in _task(boundTask)


taskWaitExt has type ((action(task of %a)) => taskWaitResult of %a) => task of %a
taskWaitExt(start) is
  _task(() => TaskWait(start, id))

-- Returns an asynchronous task, that, when executed, will call the given 'start' action.
-- The passed 'wakener' action should then be called when the result of the asynchronous
-- task is available or has failed - possibly by some other thread.
taskWait has type (action(action(task of %a))) => task of %a
taskWait(start) is taskWaitExt((wakeup) => valof { start(wakeup); valis TaskSleep; })
  
-- implement the computation contract
implementation (computation) over task is {
  _encapsulate = taskReturn;
  _abort = taskFail;
  _handle = taskCatch;
  _combine = taskBind;
}

implementation execution over task is {
  _perform = executeTask;
}

implementation injection over (task,task) is {
  _inject(C) is C;
}

implementation injection over (task,action) is {
  _inject(t) is _done(executeTask(t, raiser_fun));
}

implementation injection over (action,task) is {
  _inject(a) is runCombo(a,taskReturn,taskFail);
}

#task{?A} :: expression :- A ;* action;
#task{?B} ==> task computation {?B};
#task{} ==> task computation {};

-- Blocked for a longer time, or can continue immediately
type taskWaitResult of %a is TaskSleep or TaskMicroSleep(task of %a)

private
_doWait has type (action(task of %b), (action(task of %a)) => taskWaitResult of %a, (task of %a) => task of %b) => taskWaitResult of %b
_doWait(resumet, start, k) is valof {
  wakeup is (procedure(tv) do {
     -- when this procedure is called, the waiter shall be woken up.
     -- k creates the continuation task that depended on v. Attention: tv may be a taskFail
     resumet(k(tv));
  });
  case start(wakeup) in { -- TODO? catch java exceptions in start?
    TaskMicroSleep(tv) do valis TaskMicroSleep(k(tv));
    _ default do valis TaskSleep;
  }  
}
  
type taskStepResult of %a is
  TaskCompleted(%a) or TaskBlocked or TaskFailed(exception)
  
-- Executes a part of the asynchronous task. If it completes, the result is returned. Otherwise
-- the passed resumer action is called with the remaining task at some later point in
-- time (by some other thread). The resumer action might also be called synchronously before this
-- function returns.
-- This functions throws (does not catch) exceptions thrown by the executed part of the task.
executeTaskStep has type (action(task of %a), task of %a) => taskStepResult of %a
executeTaskStep(resumet, _task(a)) is valof {
  var curr := a;
  
  while true do {
      case curr() in {
        TaskDone(r) do { valis TaskCompleted(r); }
        TaskContinue(_task(c)) do curr := c;
        TaskWait(start, k) do {
           case _doWait(resumet, start, k) in {
             TaskMicroSleep(_task(nxt)) do curr := nxt; -- immediately woke up again
             TaskSleep default do valis TaskBlocked;
           }
        };
        TaskFailure(e) do { valis TaskFailed(e); }
      }
 --   }
  }
}

-- ForkJoin "compatible" futures

private type future of %a is alias of __forkJoinTask of %a;
private future_wait has type (future of %a) => %a
private future_set has type action(future of %a, %a)

private future_init(v) is __fjtFuture(v);
private future_wait(f) is __fjtJoinReinit(f);
private future_set(f, v) do __fjtComplete(f, v);

-- Execute and wait until the given asynchronous task completes and return it's value.
-- This functions throws (does not catch) exceptions thrown by the task.
executeTask has type (task of %a,(exception)=>%a) => %a

-- two variants, one utilizes the calling thread only (potentially spending several thread sync ops),
-- the other one uses the global thread pool and only one sync on the end of the whole computation.

executeTaskOnCurrentThread(op,EF) is valof {
  fut is future_init(op); -- first wait will return op
  resumer is (procedure(next) do {
                future_set(fut, next);
                });
  while true do {
    next is executeTaskStep(resumer, future_wait(fut));
    case next in {
      TaskFailed(e) do valis EF(e);
      TaskCompleted(res) do valis res; -- return
      TaskBlocked default do { -- blocked
        nothing; -- but loop; next future_wait will block until resume is called
      }
    }
  }
}

executeTaskOnThreadPool has type (task of %a,(exception)=>%a) => %a
-- defined below..

-- Now we can choose between the two implementations
executeTask(op,EF) is executeTaskOnThreadPool(op,EF)
-- executeTask(op,EF) is executeTaskOnCurrentThread(op,EF)

-- Returns an asynchronous task, that, when executed, calls the given function and returns it's result.
-- It 'lifts' a synchronous operation into the asynchronous world.
-- Note this is equivalent to taskBind(taskReturn(()), (function (_) is taskReturn(f()))).
taskLift has type (() => %a) => task of %a
taskLift(f) is
  _task((function () is TaskDone(f())))
  
-- Returns an asynchronous task, that, when executed, calls the given function and continues with the
-- exection of it's result.
-- It 'delays' a asynchronous operation.
-- Note this is equivalent to taskBind(taskReturn(()), (function (_) is f())).
taskGuard has type (() => task of %a) => task of %a
taskGuard(f) is
  _task((function () is TaskContinue(f())))

-- ***************** User level utilities ******************

private
type taskResult of %a is TaskSuccess(%a) or TaskError(exception);
 
private
_executeTaskOnAndThen has type action(task of %a, action(taskResult of %a));
_executeTaskOnAndThen(op, cont) do {
  task_resume is (procedure (next) do {
    _executeTaskOnAndThen(next,  cont);
  });
  task_job is (procedure () do
    case executeTaskStep(task_resume, op) in {
      TaskFailed(e) is cont(TaskError(e));
      TaskCompleted(v) is cont(TaskSuccess(v));
      TaskBlocked default is nothing;
    });
  __spawnQueuedAction(task_job);
}

private
_backgroundOn has type (task of %a) => task of %a
_backgroundOn(op) is
  let {
    -- either the task is finished before the caller waits for the result, or he waits for it before the child operation is done.
    var result_value := none;
    var result_wakeup := none;
    
    _result_undefined is false;
    _result_set is true;
    result_flag is atomic(_result_undefined);
    try_signal_result() is
      __atomic_test_n_set(result_flag, _result_undefined, _result_set);

    taskIsDone is (procedure (v) do {
      rv is (case v in {
        TaskSuccess(r) is taskReturn(r)
        TaskError(e) is taskFail(e); -- pass exception to waiter
      });
      result_value := some(rv);
      
      if not try_signal_result() then {
        -- result was already signaled, so the waiter was 'faster'
        case result_wakeup in {
          some(w) do w(rv)
          _ default do assert(false);
        }
      }
    });
    
    blockWaiter is (function(wakeup) is valof {
      result_wakeup := some(wakeup);
      if not try_signal_result() then {
        -- result was already signaled, so the child task was 'faster'
        some(v) is result_value;
        valis TaskMicroSleep(v);
      } else {
        valis TaskSleep;
      }
    });
    
    -- spawn the operation, and then either call the wakeup action or put the result in result.
    {
      _executeTaskOnAndThen(op,  taskIsDone);
    }
  } in
      -- then the completor is this: 
      taskWaitExt(blockWaiter)
    

-- Returns an asynchronous task, that, when executed, will schedule the given task on a global thread pool.
-- The result is a 'completor' task, that, when executed, will wait for and return the child
-- task's result
-- Note that if you don't need the result of the child task, you can use executeTask.
backgroundFF has type (task of %a) => task of task of %a
backgroundFF(t) is taskLift((function () is _backgroundOn(t)))

backgroundF has type (task of %a) => task of task of %a
backgroundF(t) is taskBind(backgroundFF(t), (r) => taskReturn(r))

-- Synchronous evaluation, but on the thread pool; the current thread will only block once
executeTaskOnThreadPool(op,EF) is
  let {
    -- putting in background is still in foreground..
    async_result is executeTaskOnCurrentThread(backgroundF(op), raiser_fun)
  }
  -- and waiting for the result in foreground...
  in executeTaskOnCurrentThread(async_result, EF);

#prefix((background),900);
background T is valof backgroundF(T);
