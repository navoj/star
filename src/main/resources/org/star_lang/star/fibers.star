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
 * @author dfrege
 *
 */
private import base;
private import strings;

-- temporary "empty" implementation

type fiber is _fiber {}

new_fiber() is _fiber {}

with_current_fiber(fib, body) is body()
current_fiber() is new_fiber()
_maybe_do_suspend(fib, resumet) is false

suspend(fib) do nothing
resume(fib) do nothing

/* under heavy construction...*/

/*
-- TODO: new primitive
type thread_local of %a is alias of ref %a

-- the first function will be called to create the initial value for the first
-- thread accessing the thread-local, the second function will be called for any
-- subsequently created thread with the current value of the parent thread and
-- in the context of the parent thread.
thread_local has type (() => %a, (%a) => %a) => thread_local of %a

thread_local_get has type (thread_local of %a) => %a
thread_local_set has type action(thread_local of %a, %a)
thread_local_remove has type action(thread_local of %a)
*/

/*
private import custodians;

type fiber is _fiber {
  resource has type resource;
  token has type resource_token;
  keep_alive has type ref cons of fiber; -- these must be running whenever this one is running
  
  current_custodian has type ref custodian;
}
 
_current_custodian has type (fiber) => custodian 
_current_custodian(fib) is fib.current_custodian

private __set_current_custodian(fib, cust) do {
  fib.current_custodian := cust;
}

_with_new_custodian has type (fiber, (custodian) => %a) => %a
_with_new_custodian(fib, f) is valof {
  parent is _current_custodian(fib);
  n is _new_custodian();
  add_subcustodian(parent, n);
  __set_current_custodian(fib, n);
  res is f(n);
  -- try/finally?
  __set_current_custodian(fib, parent);
  valis res;
}

with_new_custodian(f) is _with_new_custodian(current_fiber(), f)

_new_fiber(parent) is
  let {
    controller is _current_custodian(parent);
    res is _resource {
      state = atomic(ResTransient);
      controllers := _mutable_list { value := cons of { controller } };
    };
  } in valof {
    token is add_resource(controller, res);
    fib is _fiber {
      resource = res;
      token = token;
      keep_alive := cons of {};
      current_custodian := controller;
    };
    -- let the caller do this? _activate_resource(res, suspend_this);
    suspend_this is (procedure () do nothing); -- none special to do here, because the task trampoline will look at the Deactivating flag, that is set anyways
    __atomic_assign(res.state, ResActive(suspend_this));
    valis fib;
  }

-- TODO new_fiber using the current fiber as default parent
new_fiber() is _new_fiber(current_fiber())

-- TODO unique object
__main_fiber is let {
  terminate is (procedure () do nothing); -- terminate program?
  res is _resource {
    state = atomic(ResActive(terminate));
    controllers := _mutable_list { value := cons of {_main_custodian} };
  };
  fib is _fiber {
    resource = res;
    token = add_resource(_main_custodian, res);
    keep_alive := cons of {};
    current_custodian := _main_custodian;
  }
} in fib

_current_fiber is _cell(__main_fiber) -- TODO thread-local, built-in

current_fiber() is _current_fiber

private __set_current_fiber(fib) do _current_fiber := fib; -- TODO
private __unset_current_fiber() do nothing; -- TODO?

dynamic_wind has type (action(), () => %a, action()) => %a
dynamic_wind(entera, body, exita) is valof {
  entera();
  r is body();
  exita();
  valis r;
}

with_current_fiber(fib, f) is dynamic_wind((procedure () do __set_current_fiber(fib)), f, __unset_current_fiber)

_maybe_do_suspend has type (fiber, action()) => boolean
-- atomic is used instead of a volatile declaration, which does not exist
_maybe_do_suspend(fib, resume_it) is _my_atomic_test_n_set(fib.resource.state, ResDeactivating, ResInactive(resume_it))

_keep_alive_with_me has type action(fiber, fiber) -- or connect with resource?
_keep_alive_with_me(curr, other) do {
  -- remember for the future, to resume that whenever this is resumed
  curr.keep_alive := _cons(other, current_fiber().keep_alive);

  -- union the two controllers lists, and link the two fibers together
  _join_resource_controllers(curr.resource, other.resource);
  
  -- resume if suspended
  _activate_if_inactive(other.resource);
}

keep_alive_with_me(other) is _keep_alive_with_me(current_fiber(), other)

suspend has type action(fiber)
suspend(fib) do {
  -- TODO: only if this is the only controller (and thread has not been linked with another one?)
  _remove_controller_and_deactivate(fib.resource, _current_custodian(fib));
  -- block until suspended?
  nothing;
}

resume has type action(fiber)
resume(fib) do {
  -- TODO: resume on which custodian? update controllers etc...?
  _activate_if_inactive(fib.resource);
}
*/