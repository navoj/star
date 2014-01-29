/**
 * 
 * Copyright (C) 2013 Starview Inc
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
tableReplace is package{
  -- test out removing elements from a relation

  R has type ref relation of ((string,integer));
  var R := relation of {
    ("a",1);
    ("b",2);
    ("a",2);
    ("a",3);
    ("b",1);
  };
  
  check has type (string,relation of ((string,integer))) =>boolean;
  check(S,Rr) is ((SS,_) where SS=S) in Rr;

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
	
    extend R with ("c",1);
    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");
    assert pairCheck("c", 1);
    assert not pairCheck("a", 4);
    
    update ("a",X) in R with ("d",X);

    logMsg(info,"R after updating \"a\" is $R");

    assert not pairCheck("a", 1);
	  assert not pairCheck("a", 2);
	  assert not pairCheck("a", 3);
	  assert not pairCheck("a", 4);
	
	  assert pairCheck("d", 1);
	  assert pairCheck("d", 2);
	  assert pairCheck("d", 3);

  }
}