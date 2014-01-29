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
queryforms is package{
  -- test out all the query forms
  
  R is relation of {(1,"alpha"); (2,"beta"); (3,"gamma"); (4,"delta"); (10,"alpha")};
  
  main() do {
    -- varying quantifier terms
    Q0 is all X where (1,X) in R;
    logMsg(info,"Q0=$Q0");
    assert Q0=array of {"alpha"};
    
    Q1 is any of X where (1,X) in R;
    logMsg(info,"Q1=$Q1");
    assert Q1="alpha";
    
    Q2 is unique X where (_,X) in R;
    logMsg(info,"Q2=$Q2");
    assert Q2 complement (array of {"alpha"; "beta"; "gamma"; "delta"}) = array of {};
    
    Q3 is 3 of X where (_,X) in R;
    logMsg(info,"Q3=$Q3");
    assert size(Q3)=3;
    
    Q4 is unique 1 of X where (_, X) in R;
    logMsg(info,"Q4=$Q4");
    assert Q4=array of {"alpha"};
    
    -- varying sorting constraints
    S0 is all X where (X,Y) in R order by X;
    logMsg(info,"S0=$S0");
    assert S0=array of {1;2;3;4;10};
    
    S1 is all X where (X,Y) in R order descending by Y;
    logMsg(info,"S1=$S1");
    assert S1=array of {3;4;2;1;10};
    
    S2 is all X where (X,Y) in R order by Y using (>);
    logMsg(info,"S2=$S2");
    assert S2=array of {3;4;2;1;10};
    
    -- use the type of ... notation
    
    T0 is array of {all X where (1,X) in R};
    logMsg(info,"T0=$T0");
    assert T0=array of {"alpha"};
    
    T1 is cons of {all X where (1,X) in R};
    logMsg(info,"T1=$T1");
    assert T1=cons of {"alpha"};
    
    T2 has type relation of string;
    T2 is sequence of {all X where (1,X) in R};
    logMsg(info,"T2=$T2");
    assert T2=relation {"alpha"}
  }
}
    
    