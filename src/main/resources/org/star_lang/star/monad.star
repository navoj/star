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


public contract monad over %%m is {
  _return has type (%a) => %%m of %a;
  _bind has type (%%m of %a, (%a) => %%m of %b) => %%m of %b
  _fail has type () => %%m of %a;
  _perform has type (%%m of %a) => %a;
};

public implementation monad over option is {
  fun _return(x) is some(x);
  fun _bind(m, f) is switch m in {
    case none is none;
    case some(v) is f(v);
  };
  fun _fail() is none;
  fun _perform(some(X)) is X;
};
