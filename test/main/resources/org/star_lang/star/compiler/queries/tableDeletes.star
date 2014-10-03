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
tableDeletes is package{
  -- test out removing elements from a list

  R has type ref list of ((string,integer));
  var R := list of [
    ("a",1),
    ("b",2),
    ("a",2),
    ("a",3),
    ("b",1)
  ];
  
  var RR := list of [ ("a",1), ("b",2), ("c",3)];
  
  check has type (string,list of ((string,integer))) =>boolean;
  check(S,Rr) is (S,_) in Rr;

  pairCheck(A,B) is (A,B) in R;

  
  main has type action();
  main() do {
	  assert pairCheck("a", 1);
	  assert pairCheck("a", 2);
	  assert pairCheck("a", 3);
	  assert pairCheck("b", 1);
	  assert pairCheck("b", 2);
	  assert not pairCheck("c", 1);
	  assert not pairCheck("a", 4);
	
    extend R with ("b",3);
    assert pairCheck("b", 3);

    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");
    
    logMsg(info,"R before deleting a's is $R");
    delete ("a",_) in R;
    logMsg(info,"R after deleting \"a\" is $R");

	  assert not pairCheck("a", 1);
	  assert not pairCheck("a", 2);
	  assert not pairCheck("a", 3);
	  assert pairCheck("b", 1);
	  assert pairCheck("b", 2);
	  assert pairCheck("b", 3);
	  assert not pairCheck("c", 1);
	  assert not pairCheck("a", 4);
    
    delete ((_,X) where X!=2) in RR;
    logMsg(info,"RR after deleting !=2 is $RR");
    assert not ("a", 1) in RR;
    assert not ("c", 3) in RR;
    assert ("b",2) in RR;
    assert (_,2) in RR;
    assert not (_,1) in RR;

  }
}