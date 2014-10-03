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
private import strings;
private import cons;
private import dateNtime;

private import task;

-- private
import cml0;

-- To support higher level constructs like withNack and guard, the higher level rendezvous
-- internally synchronize over something more complex than just the messages:

-- we could differentiate between guarded and non-guarded (empty cvar list) rendezvous with two c'tors
type rendezvous of %a is _Rendezvous(task of ((cons of cvar, prv of ((cons of cvar, task of %a)))))

-- the channel type is the same as in cmlLib1
type channel of %a is alias of chan of %a

channel has type () => channel of %a
channel is chan

recvRv has type (channel of %a) => rendezvous of %a
sendRv has type (channel of %a, %a) => rendezvous of ()

recv has type (channel of %a) => task of %a
send has type (channel of %a, %a) => task of ()

neverRv has type rendezvous of %a
alwaysRv has type (%a) => rendezvous of %a

chooseRv has type (cons of rendezvous of %a) => rendezvous of %a

wrapRv has type (rendezvous of %a, (%a) => task of %b) => rendezvous of %b
guardRv has type (task of rendezvous of %a) => rendezvous of %a
withNackRv has type ((rendezvous of ()) => rendezvous of %a) => rendezvous of %a

-- The timeoutRv rendezvous becomes available N milliseconds after a synchronization starts
timeoutRv has type (long) => rendezvous of ()

-- The atDateRv rendezvous is available after the specified date
atDateRv has type (date) => rendezvous of ()

await has type (rendezvous of %a) => task of %a

-- Implementations

private
_basePrvTask(rv) is taskReturn((nil, wrapPrv(rv, (function (x) is taskReturn((nil, taskReturn(x)))))))
private
_basePrv(rv) is _Rendezvous(_basePrvTask(rv))

alwaysRv(v) is _basePrv(alwaysPrv(v))
 
neverRv is _Rendezvous(taskReturn((nil, neverPrv)))

recvRv(ch) is _basePrv(recvPrv(ch))

sendRv(ch, msg) is _basePrv(sendPrv(ch, msg))

wrapRv(_Rendezvous(e), f) is
  _Rendezvous(taskBind(e,
    (function ((cvs, rv)) is let {
      pwrap((cvs2, g)) is taskReturn((cvs2, taskBind(g, f)));
    } in taskReturn((cvs, wrapPrv(rv, pwrap))))))
    
guardRv(t) is
  _Rendezvous(taskBind(t, (function (_Rendezvous(rv)) is rv)))

withNackRv(f) is
  _Rendezvous(taskGuard((function () is let {
    nack is mk_cvar();
    var _Rendezvous(e) is f(_basePrv(waitPrv(nack)));
    } in taskBind(e, (function ((cvs, rv)) is taskReturn((cons(nack, cvs), rv))))
  )))

private
  consConc(nil,X) is X;
  consConc(cons(H,T),X) is cons(H,consConc(T,X));

_choose2(_Rendezvous(e1), _Rendezvous(e2)) is
  _Rendezvous(taskBind(e1,
    (function ((cvs1, rv1)) is
      taskBind(e2,
        (function ((cvs2, rv2)) is
  	    taskReturn((consConc(cvs1, cvs2),
            choosePrv(wrapPrv(rv1, (function ((cvs, th)) is taskReturn((consConc(cvs,cvs2), th)))),
                      wrapPrv(rv2, (function ((cvs, th)) is taskReturn((consConc(cvs,cvs1), th)))))
                      )))))))

chooseRv(nil) is neverRv
chooseRv(cons(x, nil)) is x
chooseRv(cons(x, xs)) default is _choose2(x, chooseRv(xs))
  

private
_timeoutRv(long(ms)) is
  taskGuard((function () is let {
    cv is mk_cvar();
    end_timed_rv() do cvar_set(cv);
    _ is __spawnDelayedAction(end_timed_rv, ms);
    } in _basePrvTask(waitPrv(cv))
  ))

timeoutRv(ms) is _Rendezvous(_timeoutRv(ms))

atDateRv(date(_ms_since_epoch)) is
  _Rendezvous(taskGuard((function () is let {
    diff_ms is timeDiff(date(_ms_since_epoch), now())
    } in _timeoutRv(diff_ms)
  )))

atDateRv(never) is neverRv

await(_Rendezvous(e)) is
  taskBind(e,
    (function ((_, rv)) is
      taskBind(prv_await(rv),
        (function ((cvs, act)) is valof {
          var _cvs := cvs;
          while _cvs matches cons(cv, rest) do {
            cvar_set(cv)
            _cvs := rest;
          };
          valis act; -- act contains the wrapped tasks
          }))))

recv(ch) is await(recvRv(ch))

send(ch, msg) is await(sendRv(ch, msg))