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
blackWhite is package{

  	type BW is black{
  		name has type string;
  	} or white{
  		name has type string;
  	}or neutral;
  	
  	var myVar := neutral;
  	
  	BWfunc has type () => integer
  	BWfunc() where myVar matches black{name=N} is 1;
  	BWfunc() where myVar matches white{name=N} is 2;
  	-- The problem is with using the same variable name N in both patterns, because if instead of 
  	-- the second case we have the following, everything seems to work correctly:
  	-- BWfunc(myVar) where myVar matches white{name=N2} is 2;
  	BWfunc() default is 3;


  main() do {
  	myVar := white{name="nadal"};
  	n1 is BWfunc();
  	
  	logMsg(info, "n1 expected: 2 actual: $n1");
  	assert n1=2;
  	
	myVar := black{name="federer"};
	n2 is BWfunc();
	
	assert BWfunc()=1;
	logMsg(info, "n2 expected: 1 actual: $n2");
  };
}
