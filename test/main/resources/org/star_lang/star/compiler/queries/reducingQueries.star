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
reducingQueries is package{
  N is list of[
    (1,2),
    (2,1),
    (1,3),
    (2,3),
    (3,2),
    (3,1)
  ];
  
  C is list of[
    (1,2),
    (2,1),
    (1,3),
    (2,3),
    (4,4),
    (3,1)
  ];
  
  plus(X,Y) is valof{
    logMsg(info,"Add $X to $Y");
    valis X+Y;
  }
  
  main() do {
  	assert reduction plus of { all X where (X,_) in N } = 12;
  	
  	assert reduction plus of { all X where (X,_) in C } = 13;
  	
  	assert reduction plus of { unique X where (X,_) in C } = 10;
  	
  	logMsg(info,"Q0=$(list of {3 of X where (X,Y) in C order by Y })");
  	
  	logMsg(info,"O=$(reduction plus of { 3 of X where (X,Y) in C order by Y })");
  	
  	assert reduction plus of { 3 of X where (X,Y) in C order by Y } = 6;
  	
  	logMsg(info,display(list of { unique 4 of X where (X,Y) in C }));

  	logMsg(info,display(reduction plus of { unique 4 of X where (X,Y) in C }));
  	
  	assert reduction plus of { unique 4 of X where (X,Y) in C } = 10;
  }
} 