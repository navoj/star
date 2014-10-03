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
support is package{
  -- the data structure that enables recording of updates
  private import dictionary;
  
  type support of %v is support{
    content has type dictionary of (%v,integer);
    content default is trEmpty;
    
    onInsert has type action(integer,%v);
    onInsert default is doNothing;
    
    onDelete has type action(integer,%v);
    onDelete default is doNothing;
  }
  
  private doNothing(_,_) do nothing;
  
  counter is let{
    var count := 0;
    
    next() is valof{
      count := count+1;
      valis count;
    }
  } in next;
  
}  