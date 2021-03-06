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
private import arithmetic;

-- if the predicate holds for the current value of the reference, it is returned and the
-- update value is set into the reference; if it does not hold, the current value is
-- returned and reference is not changed.
fun _my_atomic_test_n_swap_ext(ar, pred, upd) is
  valof {
    while (true) do {
      def curr is __atomic_reference(ar);
      if pred(curr) then {
        if __atomic_test_n_set(ar, curr, upd) then {
          valis curr;
        } else {
          -- ref changed between read and set, retry
          __yield();
        }
      }
      else
        valis curr;
    }
  }



-- Active and Inactive states include actions to deactivate resp. reactivate
type resource_state is ResActive(action()) or ResTransient or ResDeactivating or ResInactive(action())

type mutable_list of %a is _mutable_list {
   value has type ref cons of %a;
}

type resource is _resource {
  state has type atomic of resource_state;
  -- double reference, to permanently link the lists of two resources
  controllers has type ref mutable_list of custodian;
}

type resource_token is _resource_token {}

type resource_box is _resource_box {
  value has type resource;
  token has type ref resource_token; -- TODO weak ref
}

fun new_resource_box(res, token) is _resource_box {
  value = res;
  token := token;
}

type custodian_state is CustActive or CustShutdown or CustShuttingDown

type custodian is _custodian {
  state has type atomic of custodian_state;
  subcustodians has type ref cons of custodian; -- also weak?
  resources has type ref cons of resource_box;
}

implementation equality over custodian is {
  fun cust1 = cust2 is state_eq(cust1.state,cust2.state);
  fun hashCode(C) is state_hash(C)
} using {
  state_eq(st1, st2) is (st1 = st2); -- referential equality on atomic references works
  fun state_hash(C) is hashCode(C.state)
}

fun _new_custodian() is _custodian {
  state = atomic(CustActive);
  subcustodians := cons of [];
  resources := cons of [];
}
 
def _main_custodian is _new_custodian(); -- TODO: unique object

add_subcustodian has type (custodian, custodian)=>()
prc add_subcustodian(parent, child) do {
  parent.subcustodians := _cons(child, parent.subcustodians); -- TODO thread safe?
}

add_resource has type (custodian, resource) => resource_token
fun add_resource(custodian, res) is valof {
  def token is _resource_token {}; -- needs to be new object on every call
  custodian.resources := _cons(new_resource_box(res, token), custodian.resources);  -- TODO thread safe?
  valis token;
}

_remove_controller_and_deactivate has type action(resource, custodian)
fun _remove_controller_and_deactivate(res, cust) do {
  -- logMsg(info, "_remove_controller_and_deactivate: start");
  delete (c where c = cust) in res.controllers.value; -- should be correct even if list is shared
  if res.controllers.value matches _empty() then {
    -- logMsg(info, "_remove_controller_and_deactivate: emptied controller list");
    _deactivate_if_active(res);
  }
}

shutdown has type (custodian)=>()
prc shutdown(cust) do {
  if __atomic_test_n_set(cust.state, CustActive, CustShuttingDown) then {
    for subcust in cust.subcustodians do {
      shutdown(subcust);
    };
    for res_box in cust.resources do {
      _remove_controller_and_deactivate(res_box.value, cust);
    };
    cust.resources := _nil();
  }
}

_join_resource_controllers has type (resource, resource)=>()
prc _join_resource_controllers(res1, res2) do {
  def l1 is res1.controllers.value;
  def l2 is res2.controllers.value;
  def common is l1++l2; -- TODO no dups
  def common_ref is _mutable_list { value := common };
  res1.controllers := common_ref;
  res2.controllers := common_ref;
}

prc _activate_if_inactive(res) do {
  def is_inactive_state is ( (st) => switch st in { case ResInactive(_) is true; case _ default is false; });
  def st is _my_atomic_test_n_swap_ext(res.state, is_inactive_state, ResTransient)
  switch st in {
    case ResInactive(activate) do activate(); -- shall set state to ResActive
    -- what if Deactivating state?
  }
}

prc _deactivate_if_active(res) do {
  def is_active_state is ( (st) => switch st in { case ResActive(_) is true; case _ default is false; });
  def st is _my_atomic_test_n_swap_ext(res.state, is_active_state, ResDeactivating)
  switch st in {
    case ResActive(deactivate) do deactivate(); -- shall set state to ResInactive
    -- what if Transient state?
  }
}
