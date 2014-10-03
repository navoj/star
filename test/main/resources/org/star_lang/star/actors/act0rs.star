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
act0rs is package{
  
  pinger has type (()=>actor of {pong has type action(integer)})=>actor of {ping has type action(integer)};
  pinger(A) is actor{
	  on X on ping do {
		if X<300 then{
		  logMsg(info,"$X");
		  notify A() with X+1 on pong;
		}
	  }
	};

  ponger has type (()=>actor of {ping has type action(integer)})=>actor of {pong has type action(integer)};
  ponger(A) is actor{
	on X on pong do notify A() with X on ping
  }

  K(X) is (function() is X);

  group() is let{
	  PI is memo pinger(PO);
	  PO is memo ponger(PI);
  } in PI();

  main() do
	  notify group() with 0 on ping;
}