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



-- if the predicate holds for the current value of the reference, it is returned and the
-- update value is set into the reference; if it does not hold, the current value is
-- returned and reference is not changed.
_my_atomic_test_n_swap_ext(ar, pred, upd) is
  valof {
    while (true) do {
      curr is __atomic_reference(ar);
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
new_resource_box(res, token) is _resource_box {
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
  cust1 = cust2 is state_eq(cust1.state,cust2.state);
} using {
  state_eq(st1, st2) is (st1 = st2); -- referential equality on atomic references works
}

_new_custodian() is _custodian {
  state = atomic(CustActive);
  subcustodians := cons of {};
  resources := cons of {};
}
 
_main_custodian is _new_custodian(); -- TODO: unique object

add_subcustodian has type action(custodian, custodian)
add_subcustodian(parent, child) do {
  parent.subcustodians := _cons(child, parent.subcustodians); -- TODO thread safe?
}

add_resource has type (custodian, resource) => resource_token
add_resource(custodian, res) is valof {
  token is _resource_token {}; -- needs to be new object on every call
  custodian.resources := _cons(new_resource_box(res, token), custodian.resources);  -- TODO thread safe?
  valis token;
}

_remove_controller_and_deactivate has type action(resource, custodian)
_remove_controller_and_deactivate(res, cust) do {
  -- logMsg(info, "_remove_controller_and_deactivate: start");
  delete (c where c = cust) in res.controllers.value; -- should be correct even if list is shared
  if res.controllers.value matches _empty() then {
    -- logMsg(info, "_remove_controller_and_deactivate: emptied controller list");
    _deactivate_if_active(res);
  }
}

shutdown has type action(custodian)
shutdown(cust) do {
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

_join_resource_controllers has type action(resource, resource);
_join_resource_controllers(res1, res2) do {
  l1 is res1.controllers.value;
  l2 is res2.controllers.value;
  common is l1++l2; -- TODO no dups
  common_ref is _mutable_list { value := common };
  res1.controllers := common_ref;
  res2.controllers := common_ref;
}

_activate_if_inactive(res) do {
  is_inactive_state is (function (st) is case st in { ResInactive(_) is true; _ default is false; });
  st is _my_atomic_test_n_swap_ext(res.state, is_inactive_state, ResTransient)
  case st in {
    ResInactive(activate) do activate(); -- shall set state to ResActive
    -- what if Deactivating state?
  }
}

_deactivate_if_active(res) do {
  is_active_state is (function (st) is case st in { ResActive(_) is true; _ default is false; });
  st is _my_atomic_test_n_swap_ext(res.state, is_active_state, ResDeactivating)
  case st in {
    ResActive(deactivate) do deactivate(); -- shall set state to ResInactive
    -- what if Transient state?
  }
}
