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
typeCon is package {

  type Maybe of %a is
      Nothing 
  or Just(%a);

  contract Monad over %%m of %a is {
    mcreate has type (%a) => %%m of %a;
  }

  maybeCreate has type (%a) => Maybe of %a;
  maybeCreate(a) is Just(a);

  implementation Monad over Maybe of %a default is {
    mcreate = maybeCreate;
  }

  constant(a) is mcreate(a);
  
  dbl2(X) is X%X;
  
  main () do {
    thirteen is constant(13);
    
    zero is dbl2(3);
    
    assert dbl2(3)=0;
  }
}