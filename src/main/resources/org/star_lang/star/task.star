/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
fun taskReturn(v) is _task(() => TaskDone(v))

-- Failure
taskFail has type (exception) => task of %a
fun taskFail(e) is
  _task( () => TaskFailure(e))
  
-- Handle failure
taskCatch has type (task of %a, (exception) => task of %a) => task of %a
fun taskCatch(_task(b),EF) is let{
  fun catchTask() is
    switch b() in {
      case TaskFailure(E) is TaskContinue(EF(E));
      case TaskContinue(b2) is TaskContinue(taskCatch(b2, EF));
      case TaskWait(start, more) is TaskWait(start,  (c) => taskCatch(more(c), EF))
      case Other default is Other; -- TaskDone
    }
} in _task(catchTask)

-- Returns an asynchronous task, that, when executed, will 'continue' in the given
-- function as soon as the first task completes.
taskBind has type (task of %b, (%b) => task of %a) => task of %a
fun taskBind(_task(b), f) is let {
  fun boundTask() is
    switch b() in {
      case TaskDone(v) is TaskContinue(f(v)); -- TODO? catch java exceptions in f?
      case TaskContinue(b2) is TaskContinue(taskBind(b2, f));
      case TaskWait(start, more) is TaskWait(start,  (c) => taskBind(more(c), f))
      case TaskFailure(e) is TaskFailure(e)
    }
} in _task(boundTask)


taskWaitExt has type ((action(task of %a)) => taskWaitResult of %a) => task of %a
fun taskWaitExt(start) is
  _task(() => TaskWait(start, id))

-- Returns an asynchronous task, that, when executed, will call the given 'start' action.
-- The passed 'wakener' action should then be called when the result of the asynchronous
-- task is available or has failed - possibly by some other thread.
taskWait has type (action(action(task of %a))) => task of %a
fun taskWait(start) is taskWaitExt((wakeup) => valof { start(wakeup); valis TaskSleep; })
  
-- implement the computation contract
implementation (computation) over task determines exception is {
  _encapsulate = taskReturn;
  _abort = taskFail;
  _handle = taskCatch;
  _combine = taskBind;
}

implementation execution over task determines exception is {
  _perform = executeTask;
}

implementation injection over (task,task) is {
  fun _inject(C) is C;
}

implementation injection over (task,action) is {
  fun _inject(t) is _done(executeTask(t, raiser_fun));
}

implementation injection over (action,task) is {
  fun _inject(a) is runCombo(a,taskReturn,taskFail);
}

#task{?A} :: expression :- A ;* action;
#task{?B} ==> task computation {?B};
#task{} ==> task computation {};

-- Blocked for a longer time, or can continue immediately
type taskWaitResult of %a is TaskSleep or TaskMicroSleep(task of %a)

private
_doWait has type (action(task of %b), (action(task of %a)) => taskWaitResult of %a, (task of %a) => task of %b) => taskWaitResult of %b
fun _doWait(resumet, start, k) is valof {
  def wakeup is ((tv) do {
     -- when this procedure is called, the waiter shall be woken up.
     -- k creates the continuation task that depended on v. Attention: tv may be a taskFail
     resumet(k(tv));
  });
  switch start(wakeup) in { -- TODO? catch java exceptions in start?
    case TaskMicroSleep(tv) do valis TaskMicroSleep(k(tv));
    case _ default do valis TaskSleep;
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
fun executeTaskStep(resumet, _task(a)) is valof {
  var curr := a;
  
  while true do {
      switch curr() in {
        case TaskDone(r) do { valis TaskCompleted(r); }
        case TaskContinue(_task(c)) do curr := c;
        case TaskWait(start, k) do {
           switch _doWait(resumet, start, k) in {
             case TaskMicroSleep(_task(nxt)) do curr := nxt; -- immediately woke up again
             case TaskSleep default do valis TaskBlocked;
           }
        };
        case TaskFailure(e) do { valis TaskFailed(e); }
      }
 --   }
  }
}

-- ForkJoin "compatible" futures

private type future of %a is alias of __forkJoinTask of %a;
private future_wait has type (future of %a) => %a
private future_set has type action(future of %a, %a)

private fun future_init(v) is __fjtFuture(v);
private fun future_wait(f) is __fjtJoinReinit(f);
private prc future_set(f, v) do __fjtComplete(f, v);

-- Execute and wait until the given asynchronous task completes and return it's value.
-- This functions throws (does not catch) exceptions thrown by the task.
executeTask has type (task of %a,(exception)=>%a) => %a

-- two variants, one utilizes the calling thread only (potentially spending several thread sync ops),
-- the other one uses the global thread pool and only one sync on the end of the whole computation.

fun executeTaskOnCurrentThread(op,EF) is valof {
  def fut is future_init(op); -- first wait will return op
  def resumer is ((next) do { future_set(fut, next); });
  while true do {
    def next is executeTaskStep(resumer, future_wait(fut));
    switch next in {
      case TaskFailed(e) do valis EF(e);
      case TaskCompleted(res) do valis res; -- return
      case TaskBlocked default do { -- blocked
        nothing; -- but loop; next future_wait will block until resume is called
      }
    }
  }
}

executeTaskOnThreadPool has type (task of %a,(exception)=>%a) => %a
-- defined below..

-- Now we can choose between the two implementations
fun executeTask(op,EF) is executeTaskOnThreadPool(op,EF)
-- executeTask(op,EF) is executeTaskOnCurrentThread(op,EF)

-- Returns an asynchronous task, that, when executed, calls the given function and returns it's result.
-- It 'lifts' a synchronous operation into the asynchronous world.
-- Note this is equivalent to taskBind(taskReturn(()),  (_) => taskReturn(f())).
taskLift has type (() => %a) => task of %a
fun taskLift(f) is
  _task( () => TaskDone(f()))
  
-- Returns an asynchronous task, that, when executed, calls the given function and continues with the
-- exection of it's result.
-- It 'delays' a asynchronous operation.
-- Note this is equivalent to taskBind(taskReturn(()),  (_) => f()).
taskGuard has type (() => task of %a) => task of %a
fun taskGuard(f) is
  _task(() => TaskContinue(f()))

-- ***************** User level utilities ******************

private
type taskResult of %a is TaskSuccess(%a) or TaskError(exception);
 
private
_executeTaskOnAndThen has type action(task of %a, action(taskResult of %a));
prc _executeTaskOnAndThen(op, cont) do {
  def task_resume is ((next) do {
    _executeTaskOnAndThen(next,  cont);
  });
  def task_job is (() do
    switch executeTaskStep(task_resume, op) in {
      case TaskFailed(e) do cont(TaskError(e));
      case TaskCompleted(v) do cont(TaskSuccess(v));
      case TaskBlocked default do nothing;
    });
  __spawnQueuedAction(task_job);
}

private
_backgroundOn has type (task of %a) => task of %a
fun _backgroundOn(op) is
  let {
    -- either the task is finished before the caller waits for the result, or he waits for it before the child operation is done.
    var result_value := none;
    var result_wakeup := none;
    
    def _result_undefined is false;
    def _result_set is true;
    def result_flag is atomic(_result_undefined);
    fun try_signal_result() is
      __atomic_test_n_set(result_flag, _result_undefined, _result_set);

    prc taskIsDone(v) do {
      def rv is switch v in {
        case TaskSuccess(r) is taskReturn(r)
        case TaskError(e) is taskFail(e); -- pass exception to waiter
      };
      result_value := some(rv);
      
      if not try_signal_result() then {
        -- result was already signaled, so the waiter was 'faster'
        switch result_wakeup in {
          case some(w) do w(rv)
          case _ default do assert(false);
        }
      }
    };
    
    fun blockWaiter(wakeup) is valof {
      result_wakeup := some(wakeup);
      if not try_signal_result() then {
        -- result was already signaled, so the child task was 'faster'
        def some(v) is result_value;
        valis TaskMicroSleep(v);
      } else {
        valis TaskSleep;
      }
    };
    
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
fun backgroundFF(t) is taskLift(() => _backgroundOn(t))

backgroundF has type (task of %a) => task of task of %a
fun backgroundF(t) is taskBind(backgroundFF(t), (r) => taskReturn(r))

-- Synchronous evaluation, but on the thread pool; the current thread will only block once
fun executeTaskOnThreadPool(op,EF) is
  let {
    -- putting in background is still in foreground..
    def async_result is executeTaskOnCurrentThread(backgroundF(op), raiser_fun)
  }
  -- and waiting for the result in foreground...
  in executeTaskOnCurrentThread(async_result, EF);

#prefix((background),900);
fun background T is valof backgroundF(T);
