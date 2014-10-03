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
mutualRecords is package{
	type Person is noone
	            or some{
		            name has type string;
		            inform has type action(integer);
              };

  genA(V) is some{
	  name is "A";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V.inform(M-1);
		}
  }

  genB(V) is some{
	  name is "B";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V.inform(M-1);
		}
  }

  genC(V) is some{
	  name is "C";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V().inform(M-1);
		}
  }

  setup() is let{
	  A is genA(B);
	  Af() is A;
	  B is genB(C);
	  C is genC(Af);
	} in A;
	
	main() do {
		A is setup();
		A.inform(1000);
	}
}