/**
 * define the signature of a priority ordered queue 
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
 
type priorityQ of elem is priorityQ{
  queue has kind type of type;

  emptyQ has type queue of elem;
  isEmptyQ has type (queue of elem)=>boolean;
  
  insertQ has type (elem,queue of elem)=>queue of elem;
  findQMin has type (queue of elem)=>elem;
  deleteQMin has type (queue of elem)=>queue of elem;
  mergeQ has type (queue of elem,queue of elem)=>queue of elem;
}

type comparison of elem is compare{
  lt has type (elem,elem)=>boolean;
  le has type (elem,elem)=>boolean;
  eq has type (elem,elem)=>boolean;
  ge has type (elem,elem)=>boolean;
  gt has type (elem,elem)=>boolean;
}