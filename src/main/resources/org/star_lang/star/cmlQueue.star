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


-- The implementation must not be thread-safe.

private import base;
private import queue;
private import strings;

-- private import sequences;
-- private import arithmetic;
-- private import casting;

-- tuning parameters for the queue cleanup frequency
private def _cmlQueue_config_k1 is 10_;
private def _cmlQueue_config_k2 is 1.5_;

type cml_queue of %a is _CMLQueue {
  q has type queue of %a;
  size has type integer_;
  cleanup_threshold has type integer_;
  isAlive has type (%a) => boolean;
}

private
queueFoldLeft has type ((%b, %a) => %b, %b, queue of %a) => %b
fun queueFoldLeft(f, s, q) is valof {
  var qq := q;
  var r := s;
  while qq matches queuePair(e, rest) do {
    qq := rest;
    r := f(r, e);
  }
  valis r;
}

private fun queueAdjoin(queue { front=F; back=B }, E) is queue{front=F;back=cons(E,B)}
private fun queueCons(H, queue { front=F; back=B }) is queue{front=cons(H,F);back=B}

private def emptyQueue is queue { front=nil; back=nil }

private
fun queueIsEmpty(queue{front=nil; back=nil}) is true
 |  queueIsEmpty(_) default is false

private
fun reverseCons(L) is valof{
    var R := nil;
    var LL := L;
  
    while LL matches cons(E,Tl) do{
      R := cons(E,R);
      LL := Tl;
    }

    valis R
  };


private queuePair has type (%e,queue of %e)<=queue of %e;
ptn queuePair(E,queue{front=F;back=B}) from queue{front = cons(E,F); back = B}
 |  queuePair(E,queue{front=B;back=nil}) from queue{front=nil; back=Bk} where reverseCons(Bk) matches cons(E,B)

-- Filter elements out, and return new size
_filter_size has type (((%a) => boolean), queue of %a) => (integer_, queue of %a)
fun _filter_size(pred, que) is
  queueFoldLeft(
    ( (sz, res), e) =>
      (pred(e) ? (__integer_plus(sz, 1_), queueAdjoin(res, e)) : (sz, res)),
    (0_, emptyQueue),
    que)

-- we need to remove 'dead' elements from time to time
_cleanup has type (cml_queue of %a) => cml_queue of %a
fun _cleanup(cq) is let {
  def (nsize, res) is _filter_size(cq.isAlive, cq.q);
  def nth is __integer_max(
    __integer_plus(nsize, _cmlQueue_config_k1),
    __float_integer(__float_floor(__float_times(_cmlQueue_config_k2, __integer_float(nsize)))));
} in _CMLQueue {
  q := res;
  size := nsize;
  -- next threshold, based on the new size and the tuning parameters    
  cleanup_threshold := nth;
  isAlive := cq.isAlive;
}

_maybeCleanup has type (cml_queue of %a) => cml_queue of %a
fun _maybeCleanup(q) is valof {
  if __integer_ge(q.size, q.cleanup_threshold) then
    valis _cleanup(q)
  else
    valis q;
}

enqueue has type (cml_queue of %a, %a) => cml_queue of %a
fun enqueue(q0, v) is let {
  def q1 is _maybeCleanup(q0);
} in _CMLQueue {
  q := queueAdjoin(q1.q, v);
  size := __integer_plus(q1.size, 1_);
  cleanup_threshold := q1.cleanup_threshold;
  isAlive := q1.isAlive;
}

dequeue has type (cml_queue of %a) => (option of %a, cml_queue of %a)
fun dequeue(cq) is valof {
  if queue_isEmpty(cq)
  then valis (none, cq)
  else {
    def queuePair(r, rest) is cq.q;
    valis (some(r), _CMLQueue {
      q := rest;
      size := __integer_minus(cq.size, 1_);
      cleanup_threshold := cq.cleanup_threshold;
      isAlive := cq.isAlive;
    });
  }
}

-- dequeue the first entry matching the predicate
dequeue_match has type (cml_queue of %a, (%a) => boolean) => (option of %a, cml_queue of %a)
fun dequeue_match(cq, pred) is valof {
  var noMatch := nil;
  var _q := cq.q;
  var nsize := cq.size;
  var res := none;
  var found := false;
  while (not found) and (not queueIsEmpty(_q)) do {
    def queuePair(r, rest) is _q;
    _q := rest;
    if pred(r) then {
      res := some(r)
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
fun undequeue(cq, v) is _CMLQueue {
  q := queueCons(v, cq.q)
  size := __integer_plus(cq.size, 1_);
  cleanup_threshold := cq.cleanup_threshold;
  isAlive := cq.isAlive;
}

mk_queue has type ((%a) => boolean) => cml_queue of %a
fun mk_queue(isAlive) is _CMLQueue {
  q := emptyQueue;
  size := 0_;
  cleanup_threshold := _cmlQueue_config_k1;
  isAlive = isAlive;
}

queue_isEmpty has type (cml_queue of %a) => boolean
fun queue_isEmpty(q) is __integer_eq(q.size, 0_)
 