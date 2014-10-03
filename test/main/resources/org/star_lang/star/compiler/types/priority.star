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
priority is package{
  type priorityQ of t is priorityQ{
    heap has kind type where pPrint over heap;
    emptyQ has type heap;
    isEmptyQ has type (heap)=>boolean;
    insertQ has type (t,heap)=>heap;
    mergeQ has type (heap,heap)=>heap;
    firstEl has type (heap)=>t;
    restQ has type (heap)=>heap;
  }
  
  type ordering of t is ordering{
    lt has type (t,t)=>boolean;
    le has type (t,t)=>boolean;
    eq has type (t,t)=>boolean;
    ge has type (t,t)=>boolean;
    gt has type (t,t)=>boolean;
  }
}