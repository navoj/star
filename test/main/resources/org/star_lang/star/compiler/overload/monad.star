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
monad is package {

  type Maybe of %a is
  	   Nothing 
    or Just(%a);

  contract monad over %%m is {
    return has type for all a such that (a) => %%m of a;
    bind has type for all a,b such that (%%m of a, (a) => %%m of b) => %%m of b
    fail has type for all a such that () => %%m of a;
  };

  implementation monad over Maybe is {
    return(x) is Just(x);
    bind(m, f) is
			case m in {
			  Nothing is Nothing;
			  Just(v) is f(v);
			 };
    fail() is Nothing;
  };
}
