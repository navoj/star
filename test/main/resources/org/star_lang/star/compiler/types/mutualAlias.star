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
mutualAlias is package{
  type args is alias of ((cons of integer, state_func));
  type state_func is alias of ((args) => args);

  type other_state is Baz {
    state_f has type state_func;
  };

  state_func_cons has type (integer, state_func) => state_func;
  fun state_func_cons(i, f) is (((numbers, _)) => (cons(i, numbers), f));
}