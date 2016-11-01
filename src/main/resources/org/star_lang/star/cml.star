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
private import strings;
private import cons;
private import dateNtime;
private import arithmetic;

private import task;

-- private
import cml0;

-- To support higher level constructs like withNack and guard, the higher level rendezvous
-- internally synchronize over something more complex than just the messages:

-- we could differentiate between guarded and non-guarded (empty cvar list) rendezvous with two c'tors
public type rendezvous of %a is _Rendezvous(task of ((cons of cvar, prv of ((cons of cvar, task of %a)))))

-- the channel type is the same as in cmlLib1
public type channel of %a is alias of chan of %a

public channel has type () => channel of %a
def channel is chan

public recvRv has type (channel of %a) => rendezvous of %a
public sendRv has type (channel of %a, %a) => rendezvous of ()

public recv has type (channel of %a) => task of %a
public send has type (channel of %a, %a) => task of ()

public neverRv has type rendezvous of %a
public alwaysRv has type (%a) => rendezvous of %a

public chooseRv has type (cons of rendezvous of %a) => rendezvous of %a

public wrapRv has type (rendezvous of %a, (%a) => task of %b) => rendezvous of %b
public guardRv has type (task of rendezvous of %a) => rendezvous of %a
public withNackRv has type ((rendezvous of ()) => rendezvous of %a) => rendezvous of %a

-- The timeoutRv rendezvous becomes available N milliseconds after a synchronization starts
public timeoutRv has type (long) => rendezvous of ()

-- The atDateRv rendezvous is available after the specified date
public atDateRv has type (date) => rendezvous of ()

public await has type (rendezvous of %a) => task of %a

-- Implementations

private
fun _basePrvTask(rv) is taskReturn((nil, wrapPrv(rv, (x) => taskReturn((nil, taskReturn(x))))))

private
fun _basePrv(rv) is _Rendezvous(_basePrvTask(rv))

fun alwaysRv(v) is _basePrv(alwaysPrv(v))
 
def neverRv is _Rendezvous(taskReturn((nil, neverPrv)))

fun recvRv(ch) is _basePrv(recvPrv(ch))

fun sendRv(ch, msg) is _basePrv(sendPrv(ch, msg))

fun wrapRv(_Rendezvous(e), f) is
  _Rendezvous(taskBind(e,
    ( ((cvs, rv)) => let {
      fun pwrap((cvs2, g)) is taskReturn((cvs2, taskBind(g, f)));
    } in taskReturn((cvs, wrapPrv(rv, pwrap))))))
    
fun guardRv(t) is
  _Rendezvous(taskBind(t,  (_Rendezvous(rv)) => rv))

fun withNackRv(f) is
  _Rendezvous(taskGuard((() => let {
    def nack is mk_cvar();
    def _Rendezvous(e) is f(_basePrv(waitPrv(nack)));
    } in taskBind(e, ((cvs, rv)) => taskReturn((cons(nack, cvs), rv)))
  )))

private
  fun consConc(nil,X) is X
   |  consConc(cons(H,T),X) is cons(H,consConc(T,X))

fun _choose2(_Rendezvous(e1), _Rendezvous(e2)) is
  _Rendezvous(taskBind(e1,
    ( ((cvs1, rv1)) =>
      taskBind(e2,
        (((cvs2, rv2)) =>
  	    taskReturn((consConc(cvs1, cvs2),
            choosePrv(wrapPrv(rv1, (((cvs, th)) => taskReturn((consConc(cvs,cvs2), th)))),
                      wrapPrv(rv2, (((cvs, th)) => taskReturn((consConc(cvs,cvs1), th)))))
                      )))))))

fun chooseRv(nil) is neverRv
 |  chooseRv(cons(x, nil)) is x
 |  chooseRv(cons(x, xs)) default is _choose2(x, chooseRv(xs))
  

private
fun _timeoutRv(long(ms)) is
  taskGuard((() => let {
    def cv is mk_cvar();
    prc end_timed_rv() do cvar_set(cv);
    { ignore __spawnDelayedAction(end_timed_rv, ms) }
    } in _basePrvTask(waitPrv(cv))
  ))

fun timeoutRv(ms) is _Rendezvous(_timeoutRv(ms))

fun atDateRv(date(_ms_since_epoch)) is
  _Rendezvous(taskGuard((() => let {
    def diff_ms is timeDiff(date(_ms_since_epoch), now())
    } in _timeoutRv(diff_ms)
  )))
 |  atDateRv(never) is neverRv

fun await(_Rendezvous(e)) is
  taskBind(e,
    ( ((_, rv)) =>
      taskBind(prv_await(rv),
        ( ((cvs, act)) => valof {
          var _cvs := cvs;
          while _cvs matches cons(cv, rest) do {
            cvar_set(cv)
            _cvs := rest;
          };
          valis act; -- act contains the wrapped tasks
          }))))

fun recv(ch) is await(recvRv(ch))

fun send(ch, msg) is await(sendRv(ch, msg))