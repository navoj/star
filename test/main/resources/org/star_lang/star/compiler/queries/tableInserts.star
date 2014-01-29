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
tableInserts is package{
  -- test out ading elements to a relation

  R has type ref relation of ((string,integer));
  var R := relation of {
    ("a",1);
  };
  
  check has type (string,relation of ((string,integer))) =>boolean;
  check(S,Rl) is ((SS,_) where SS=S) in Rl;
  
  main has type action();
  main() do {
    assert not ("b",2) in R;
    extend R with ("b",2);
    assert ("b",2) in R;
    logMsg(info,"is a in $R? $(check("a",R))");
    logMsg(info,"is e in $R? $(check("e",R))");
    
    merge R with relation{("e",5)};
    logMsg(info,"is e in $R? $(check("e",R))");
    assert ("e",5) in R;
    
    merge R with relation{("aa",1);("bb",2);("cc",3)};
    logMsg(info,"is cc in $R? $(check("cc",R))");
    assert ("aa",_) in R;
    
    RR has type ref relation of ((string,integer));
    var RR := relation of {};
    for KV in (all (K,V) where (K,V) in R and V>1) do
      extend RR with KV;
    logMsg(info,"RR=$RR");
  }
}