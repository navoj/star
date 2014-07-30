/**
 *  A queue with some special features.
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

-- The implementation must not be thread-safe.

private import base;
private import queue;
private import strings;

-- private import sequences;
-- private import arithmetic;
-- private import casting;

-- tuning parameters for the queue cleanup frequency
private _cmlQueue_config_k1 is 10_;
-- not possible (inlined): private _cmlQueue_config_k2 is 1.5_;

type cml_queue of %a is _CMLQueue {
  q has type queue of %a;
  size has type integer_;
  cleanup_threshold has type integer_;
  isAlive has type (%a) => boolean;
}

private
queueFoldLeft has type ((%b, %a) => %b, %b, queue of %a) => %b
queueFoldLeft(f, s, q) is valof {
  var qq := q;
  var r := s;
  while qq matches queuePair(e, rest) do {
    qq := rest;
    r := f(r, e);
  }
  valis r;
}

private queueAdjoin(queue { front=F; back=B }, E) is queue{front=F;back=cons(E,B)}
private queueCons(H, queue { front=F; back=B }) is queue{front=cons(H,F);back=B}

private emptyQueue is queue { front=nil; back=nil }

private queueIsEmpty(queue{front=nil; back=nil}) is true
queueIsEmpty(_) default is false

private
reverseCons(L) is valof{
    var R := nil;
    var LL := L;
  
    while LL matches cons(E,Tl) do{
      R := cons(E,R);
      LL := Tl;
    }

    valis R
  };


private queuePair has type (%e,queue of %e)<=queue of %e;
queuePair(E,queue{front=F;back=B}) from queue{front = cons(E,F); back = B};
queuePair(E,queue{front=B;back=nil}) from queue{front=nil; back=Bk} where reverseCons(Bk) matches cons(E,B);

type Maybe of %a is Just(%a) or Nothing

-- Filter elements out, and return new size
_filter_size has type (((%a) => boolean), queue of %a) => (integer_, queue of %a)
_filter_size(pred, que) is
  queueFoldLeft(
    (function ((sz, res), e) is
      pred(e) ? (__integer_plus(sz, 1_), queueAdjoin(res, e)) | (sz, res)),
    (0_, emptyQueue),
    que)

-- we need to remove 'dead' elements from time to time
_cleanup has type (cml_queue of %a) => cml_queue of %a
_cleanup(cq) is let {
  (nsize, res) is _filter_size(cq.isAlive, cq.q);
  -- 1.5 = _cmlQueue_config_k2
  nth is __integer_max(
    __integer_plus(nsize, _cmlQueue_config_k1),
    __float_integer(__float_floor(__float_times(1.5_, __integer_float(nsize)))));
} in _CMLQueue {
  q := res;
  size := nsize;
  -- next threshold, based on the new size and the tuning parameters    
  cleanup_threshold := nth;
  isAlive := cq.isAlive;
}

_maybeCleanup has type (cml_queue of %a) => cml_queue of %a
_maybeCleanup(q) is valof {
  if __integer_ge(q.size, q.cleanup_threshold) then
    valis _cleanup(q)
  else
    valis q;
}

enqueue has type (cml_queue of %a, %a) => cml_queue of %a
enqueue(q0, v) is let {
  q1 is _maybeCleanup(q0);
} in _CMLQueue {
  q := queueAdjoin(q1.q, v);
  size := __integer_plus(q1.size, 1_);
  cleanup_threshold := q1.cleanup_threshold;
  isAlive := q1.isAlive;
}

dequeue has type (cml_queue of %a) => (Maybe of %a, cml_queue of %a)
dequeue(cq) is valof {
  if queue_isEmpty(cq)
  then valis (Nothing, cq)
  else {
    queuePair(r, rest) is cq.q;
    valis (Just(r), _CMLQueue {
      q := rest;
      size := __integer_minus(cq.size, 1_);
      cleanup_threshold := cq.cleanup_threshold;
      isAlive := cq.isAlive;
    });
  }
}

-- dequeue the first entry matching the predicate
dequeue_match has type (cml_queue of %a, (%a) => boolean) => (Maybe of %a, cml_queue of %a)
dequeue_match(cq, pred) is valof {
  var noMatch := nil;
  var _q := cq.q;
  var nsize := cq.size;
  var res := Nothing;
  var found := false;
  while (not found) and (not queueIsEmpty(_q)) do {
    queuePair(r, rest) is _q;
    _q := rest;
    if pred(r) then {
      res := Just(r)
      nsize := __integer_minus(nsize, 1_);
      found := true;
    }
    else {
      noMatch := cons(r, noMatch);
    }
  };
  -- put back non matching elements
  while noMatch matches cons(x, xs) do {
    _q := queueCons(x, _q); -- undequeue
    noMatch := xs;
  };
  valis (res, _CMLQueue {
    q := _q;
    size := nsize;
    cleanup_threshold := cq.cleanup_threshold;
    isAlive := cq.isAlive;
  });  
}

undequeue has type (cml_queue of %a, %a) => cml_queue of %a
undequeue(cq, v) is _CMLQueue {
  q := queueCons(v, cq.q)
  size := __integer_plus(cq.size, 1_);
  cleanup_threshold := cq.cleanup_threshold;
  isAlive := cq.isAlive;
}

mk_queue has type ((%a) => boolean) => cml_queue of %a
mk_queue(isAlive) is _CMLQueue {
  q := emptyQueue;
  size := 0_;
  cleanup_threshold := _cmlQueue_config_k1;
  isAlive = isAlive;
}

queue_isEmpty has type (cml_queue of %a) => boolean
queue_isEmpty(q) is __integer_eq(q.size, 0_)
 