/**
 * 
 * Copyright (C) 2013 Starview Inc
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
private import iterable;

import task;

import cmlSpinLock
import cmlQueue
private import cmlAtomicRef

-- Continuations


private
type cont of %a is alias of action(task of %a)

private
mk_task_cont has type (action(task of %a)) => cont of %a
mk_task_cont(wakeup) is wakeup

-- returns a continuation, which, when continued, calls the function F before continuing with K.
private
cont_wrap has type (cont of %a, (%b) => task of %a) => cont of %b
cont_wrap(k, f) is
  (procedure (tb) do k(taskBind(tb, f)))

private
cont_throw_task has type action(cont of %a, task of %a)
cont_throw_task(k, t) do { k(t); }

private
cont_throw_val has type action(cont of %a, %a)
cont_throw_val(k, v) do { cont_throw_task(k, taskReturn(v)); }

-- The most primitive CML functions and types

-- private
type rv_status is alias of _integer

private WAITING is 0_
private CLAIMED is 1_
private SYNCHED is 2_

-- private
type rv_state is alias of atomic_int

-- A base rendezvous
type brv of %a is brv {
    -- ask if a value is available
    pollFn has type () => boolean;
    -- get the value if there is one
    doFn has type () => Maybe of task of %a;
    -- register continuation
    blockFn has type action(rv_state, cont of %a);
}

-- A primitive rendezvous
type prv of %a is
  BRV(brv of %a)  or
  CHOOSE(prv of %a, _integer, prv of %a, _integer) 

choosePrv has type (prv of %a, prv of %a) => prv of %a

prv_await has type (prv of %a) => task of %a
prv_sync has type (prv of %a) => %a

type send_request of %a is send_request {
  state has type rv_state;
  msg has type %a;
  k has type cont of ();
}

type recv_request of %a is recv_request {
  state has type rv_state;
  k has type cont of %a;
}

private
_same_state(req, s) is atomic_int_ref_eq(req.state, s)

type chan of %a is _chan {
  -- name has type string;
  lock has type spin_lock;
  sendq has type ref cml_queue of send_request of %a;
  recvq has type ref cml_queue of recv_request of %a;
}

private
_send_request_is_alive(sr) is
  not __integer_eq(__atomic_int_reference(sr.state), SYNCHED)
  
private
_recv_request_is_alive(rr) is
  not __integer_eq(__atomic_int_reference(rr.state), SYNCHED)

chan has type () => chan of %a
chan() is _chan {
  -- sometimes good for debugging: name = name;
  lock = mk_spin_lock();
  -- the predicates are used to cleanup the queues from useless entries from time to time
  sendq := mk_queue(_send_request_is_alive);
  recvq := mk_queue(_recv_request_is_alive);
}

recvPrv has type (chan of %a) => prv of %a
sendPrv has type (chan of %a, %a) => prv of ()

type cvar_waiter is cvar_waiter {
  rv_state has type rv_state;
  k has type cont of ();
}

type cvar is cvar {
  lock has type spin_lock;
  state has type ref boolean;
  waiting has type ref cons of cvar_waiter;
}

mk_cvar has type () => cvar

-- set a condition variable (wakeup who is waiting for it)
cvar_set has type action(cvar)

-- waitPrv is used to build higher level abstractions like withNack
waitPrv has type (cvar) => prv of ()

alwaysPrv has type (%a) => prv of %a
neverPrv has type prv of %a

-- Implementations

-- Optimistically poll the base rendezvous

/*
private
firstMapMaybe has type ((%a) => Maybe of %b, cons of %a) => Maybe of %b;
firstMapMaybe(f, lis) is valof {
  var r:= lis;
  while r matches cons(x, xs) do {
      case f(x) in {
        Nothing do nothing;
        a matching Just(y) do valis a;
      };
      r := xs;
  };
  valis Nothing;
};

private
poll has type (prv of %a) => cons of brv of %a
poll(prv) is let {
  poll_(BRV(brv0), enabled) is
    brv0.pollFn() ? cons(brv0, enabled) | enabled
  poll_(CHOOSE(rv1, _, rv2, _), enabled) is
    poll_(rv1, poll_(rv2, enabled))
} in poll_(prv, nil)
*/

private
randomFirst_ has type ((brv of %a) => Maybe of %c, prv of %a, prv of %a, _integer) => Maybe of %c

/* To choose a random element, we first find a fair random path to a leaf, try to enable that (doFn()), but
   if that does not work, we start over with the rest tree we constructed on the way down, which contains
   all leafs except the one just tested. */

/* recursive version
randomFirst_(pred, BRV(brv0), rest, sz_rest) is
  case pred(brv0) in {
    Just(v) is Just(v)
    _ default is __integer_eq(sz_rest, 0_) ? Nothing | randomFirst_(pred, rest, neverPrv, 0_)
  }
  
randomFirst_(pred, CHOOSE(c1, sz1, c2, sz2), rest, sz_rest) is
  -- need to randomly interleave left, right and rest of the "top" (which was not chosen previously)
  valof {
    if __integer_lt(__integer_random(__integer_plus(sz1, sz2)), sz1) then {
      if __integer_eq(sz_rest, 0_) then
        valis randomFirst_(pred, c1, c2, sz2)
      else
        valis randomFirst_(pred, c1, CHOOSE(c2, sz2, rest, sz_rest), __integer_plus(sz2, sz_rest))
    } else {
      if sz_rest = 0 then
        valis randomFirst_(pred, c2, c1, sz1)
      else
        valis randomFirst_(pred, c2, CHOOSE(c1, sz1, rest, sz_rest), __integer_plus(sz1, sz_rest))
    }
  }
*/

/* iterative version */
randomFirst_(pred, prv0_, rest_, sz_rest_) is valof {
  var prv0 := prv0_;
  var rest := rest_;
  var sz_rest := sz_rest_;
  
  while (true) do {
    case prv0 in {
      BRV(brv0) do {
        case pred(brv0) in {
          Nothing do {
            if __integer_eq(sz_rest, 0_) then
              valis Nothing
            else {
              prv0 := rest;
              -- not needed: rest := neverPrv;
              sz_rest := 0_;
            }
          }
          s default do valis s;
        }
      }
      
      CHOOSE(c1, sz1, c2, sz2) do {
        if __integer_lt(__integer_random(__integer_plus(sz1, sz2)), sz1) then {
          prv0 := c1;
          if __integer_eq(sz_rest, 0_) then {
            rest := c2;
            sz_rest := sz2;
          } else {
            rest := CHOOSE(c2, sz2, rest, sz_rest);
            sz_rest := __integer_plus(sz2, sz_rest);
          }
        } else {
          prv0 := c2;
          if __integer_eq(sz_rest, 0_) then {
            rest := c1;
            sz_rest := sz1;
          } else {
            rest := CHOOSE(c1, sz1, rest, sz_rest);
            sz_rest := __integer_plus(sz1, sz_rest);
          }
        }
      }
    }
  }
}

private
randomFirst has type ((brv of %a) => Maybe of %c, prv of %a) => Maybe of %c
randomFirst(pred, prv0) is randomFirst_(pred, prv0, neverPrv, 0_)

private
randomPollFirst has type (prv of %a) => Maybe of task of %a
randomPollFirst(prv0) is randomFirst(_opoll, prv0)

private
_opoll(brvx) is brvx.pollFn() ? brvx.doFn() | Nothing

-- Return Just(v) of the first base rendezvous where doFN is possible, or Nothing.
private
_get_first_enabled(prv) is 
  -- To not always favor one alternative over the other, we randomly choose in the enabled list.
  -- Another approach could be: Make 'enabled' a priority queue; pollFn determined a priority based on how many times a channel has beed enabled without being selected.
  randomPollFirst(prv)
  
  -- Unfairly get the first one:
  -- firstMapMaybe((function (brv0) is brv0.doFn()), poll(prv))

-- Block on all base rendezvous
private
block has type action(prv of %a, rv_state, cont of %a)
block(BRV(brv0), state, k) do {
  brv0.blockFn(state, k);
}
block(CHOOSE(rv1, sz1, rv2, sz2), state, k) do {
  block(rv1, state, k);
  block(rv2, state, k);
}
    
prv_await(prv) is let {
  start is (function (wakeup) is valof {
    case _get_first_enabled(prv) in {
      Just(t) do valis TaskMicroSleep(t)
      _ default do {
        k is mk_task_cont(wakeup);
        block(prv, atomic_int(WAITING), k);
        valis TaskSleep;
      }
    }
  });
  base is taskWaitExt(start);
} in (base)

wrapPrv has type (prv of %a, (%a) => task of %b) => prv of %b
wrapPrv(BRV(base), f) is BRV( brv {
  pollFn = base.pollFn;
  doFn = (function () is base.doFn() matches Just(t) ? Just(taskBind(t, f)) | Nothing);
  blockFn = (procedure (state, k) do {
    base.blockFn(state, cont_wrap(k, f))
  });
})

wrapPrv(CHOOSE(rv1, sz1, rv2, sz2), f) is
  CHOOSE(wrapPrv(rv1, f), sz1, wrapPrv(rv2, f), sz2)
  
private
_size has type (prv of %a) => _integer
_size(BRV(b1)) is 1_
_size(CHOOSE(c1, sz1, c2, sz2)) is __integer_plus(sz1, sz2)

choosePrv(prv1, prv2) is CHOOSE(prv1, _size(prv1), prv2, _size(prv2))

choosePrvList has type (cons of prv of %a) => prv of %a
choosePrvList(nil) is neverPrv
choosePrvList(cons(x, nil)) is x
choosePrvList(cons(x, xs)) default is choosePrv(x, choosePrvList(xs))

-- The reference implementation (Parallel CML) did dispatch() calls at various places - they probably only serve for a better performance (TODO? Check)
private
_dispatch() do {
  -- sleep(0L)
  -- nothing;
  __yield()
}

-- send and receive rendezvous are almost the same, only some things mirrored; this is an internal abstraction over them
private
_channelPrv(act, ch, ref in_queue, ref out_queue, get_result, passed_msg, make_request) is
  let {
    pollFn() is valof {
      -- logMsg(info, "Poll for $(act) over $(ch.name)");
      valis not queue_isEmpty(in_queue)
    }
    
    doFn() is valof { -- try to find and sync with a partner
      -- logMsg(info, "Try $(act) over $(ch.name)");
      spinLock(ch.lock);
      while dequeue(in_queue) matches (Just(his_request), n_in_queue) do {
        in_queue := n_in_queue;
        -- try to fulfill that other request
        while not __integer_eq(__atomic_int_reference(his_request.state), SYNCHED) do {
          -- try to set state from WAITING to SYNCHED, looking at the new value:
          case atomic_int_cas(his_request.state, WAITING, SYNCHED) in {
            v where __integer_eq(v, WAITING) do {
              -- success
		        spinUnlock(ch.lock);
              -- let the partner continue and return
              cont_throw_val(his_request.k, passed_msg);
              -- logMsg(info, "continued partner with $(__display(passed_msg)), now return $(__display(get_result(his_request)))");
              valis Just(taskReturn(get_result(his_request)));
            }
            v where __integer_eq(v, CLAIMED) do {
              -- somebody was just looking at it, try again
              -- logMsg(info, "doFn: his_request: CLAIMED - retry");
              _dispatch() -- pause/yield
            }
            v where __integer_eq(v, SYNCHED) default do {
              -- already synched, try next one (will stay synched)
              nothing -- or break
            }
          }
        }
        -- someone just synched with this one, try if there is another one (LOOP)
      }
      -- nothing in the queue
      spinUnlock(ch.lock);
      valis Nothing
    }
    
    blockFn(state, k) do {
      -- logMsg(info, "Try again $(act) over $(ch.name)");
      spinLock(ch.lock);
      -- first try again to find a partner (because poll is not synchronized)
      done is valof {
        -- We need to ignore requests contained in the same sync operation/rendezvous (the same state flag)
        while dequeue_match(in_queue, (function(e) is not _same_state(e, state))) matches (Just(his_request), n_in_queue) do {
          in_queue := n_in_queue;
          -- a partner blocked since we polled
          while not __integer_eq(__atomic_int_reference(his_request.state), SYNCHED) do {
            case atomic_int_cas(state, WAITING, CLAIMED) in { -- test_n_set might be enough here
              v1 where __integer_eq(v1, WAITING) do {
                -- ok, now also try to get the matching rendezvous
                case atomic_int_cas(his_request.state, WAITING, SYNCHED) in {
                  v where __integer_eq(v, WAITING) do {
                    -- success
                    spinUnlock(ch.lock);
                    __atomic_int_assign(state, SYNCHED);
                    cont_throw_val(his_request.k, passed_msg); -- let the partner continue
                    cont_throw_val(k, get_result(his_request)); -- TODO: original: enqueueRdy k ??
                    valis true;
                  }
                  v where __integer_eq(v, CLAIMED) do {
                    -- logMsg(info, "blockFN his_request: CLAIMED - try again");
                    __atomic_int_assign(state, WAITING) -- waiting again
                    -- retry this one (spinning)
                    _dispatch(); -- pause/yield
                  }
                  v where __integer_eq(v, SYNCHED) default do {
                    -- logMsg(info, "his_request: SYNCHED");
                    -- someone took it away just now
                    __atomic_int_assign(state, WAITING) -- waiting again
                    -- while condition will not hold now
                  }
                }
              }
              -- CLAIMED assert false
              _ default do {
                -- Someone else has synchronized on this rendezvous, so we don't have to do anything
                in_queue := undequeue(in_queue, his_request);
                spinUnlock(ch.lock);
                _dispatch();
                valis true;
              }
            }
          }
        };
        valis false
      }
      if not done then {
        -- nobody found, so put our request in the queue
        -- logMsg(info, "Blocked $(act) over $(ch.name)");
        k_ is (procedure (v) do {
           -- logMsg(info, "Unblocked $(act) request on channel");
           k(v) }); 
        out_queue := enqueue(out_queue, make_request(state, k_));
        spinUnlock(ch.lock);
      }
    }
  
  } in BRV( brv { pollFn = pollFn; doFn = doFn; blockFn = blockFn; } )
  
sendPrv(ch, msg) is let {
    -- the result is always ()
    get_result(his_request) is ()
    -- when there is nothing in receive queue, we need a send request:
    make_request(state, k) is send_request { state = state; msg = msg; k = k };
  } in
    _channelPrv("send", ch, ref ch.recvq, ref ch.sendq, get_result, msg, make_request)

recvPrv(ch) is let {
    -- the result is the message from the other side
    get_result(his_request) is his_request.msg;
    -- when there is nothing in send queue, we need a receive request:
    make_request(state, k) is recv_request { state = state; k = k };
  } in
    _channelPrv("receive", ch, ref ch.sendq, ref ch.recvq, get_result, (), make_request)
  
mk_cvar() is cvar {
  lock = mk_spin_lock();
  state := false;
  waiting := nil;
}

cvar_set(cv) do {
  var waiting := nil;
  
  spinLock(cv.lock);
  cv.state := true;
  waiting := cv.waiting;
  spinUnlock(cv.lock);
  
  for w in waiting do {
    if __atomic_int_test_n_set(w.rv_state, WAITING, SYNCHED) then {
      cont_throw_task(w.k, _taskUnit)
    -- else do nothing -- already synched by someone else
    }
  }
}

private
_taskUnit is taskReturn(())
private
_justUnit is Just(_taskUnit)

waitPrv(cv) is let {
  pollFn() is cv.state
  
  doFn() is _justUnit -- always possible after pollFn returned true
  
  blockFn(state, k) do {
    spinLock(cv.lock);
    if cv.state then {
      spinUnlock(cv.lock);
      case atomic_int_cas(state, WAITING, SYNCHED) in {
        v where __integer_eq(v, WAITING) do {
          cont_throw_task(k, _taskUnit);
        }
        _ default do _dispatch();
      }
    }
    else {
      cv.waiting := cons(cvar_waiter { rv_state = state; k = k }, cv.waiting);
      spinUnlock(cv.lock);
    }
  }
  
} in BRV( brv { pollFn = pollFn; doFn = doFn; blockFn = blockFn; } )
 
alwaysPrv(msg) is let {
  pollFn() is true
  doFn() is Just(taskReturn(msg))
  blockFn(state, k) do {
    raise "blockFn not applicable on alwaysPrv"
  }
} in BRV( brv { pollFn = pollFn; doFn = doFn; blockFn = blockFn; } )
  
neverPrv is let {
  pollFn() is false
  doFn() is Nothing
  blockFn(state, k) do nothing;
} in BRV( brv { pollFn = pollFn; doFn = doFn; blockFn = blockFn; } )
