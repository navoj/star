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
noTerminate is package {

  type Store of %a is Empty or Store(%a);

  contract Monad over %%m of %a is {
    bind has type (%%m of %a, ((%a) => %%m of %b)) => %%m of %b;
  }

  type MyMonad of %a is MyMonad { result has type Store of %a };

  maybeBind has type (MyMonad of %a, (%a) => MyMonad of %b) => MyMonad of %b;
  maybeBind(mm matching MyMonad { result = result_a }, f) is
    case a of {
      Store(val) is f(val);
      Empty is mm;
    };

  implementation Monad over MyMonad of %a is {
    bind = maybeBind;
  }
}