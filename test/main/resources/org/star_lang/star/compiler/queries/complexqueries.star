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
complexqueries is package{
  -- test out indexing and equality queries
  
  R has type indexed of((string,integer));
  R is indexed {
    ("a",1);
    ("b",2);
    ("c",3);
    ("a",4);
    ("a",5);
    ("b",6);
    ("c",7);
    ("a",8);
    ("a",9);
    ("b",10);
    ("aa",1);
    ("bb",2);
    ("cc",3);
    ("aa",4);
    ("aa",5);
    ("bb",6);
    ("cc",7);
    ("aa",8);
    ("aa",9);
    ("bb",10);
    ("a",1);
    ("b",12);
    ("c",13);
    ("a",14);
    ("a",15);
    ("b",16);
    ("c",17);
    ("a",1);
    ("a",19);
    ("b",1);
    ("b",2);
  };
  
  S is list of [
    ("a",10),
    ("b",2),
    ("d",3)
  ];
  
  T is list of [
    10,
    12
   ];
   
  U is indexed{
    ("a",10);
    ("a",12);
    ("a",0)
  };
  
  -- DD is all (X,Y) where (X,Y) in S and (X,Y) in R;
  
  DD is all (X,Y) where  (X,Y) in R and (X,Y) in S;
  
  QQ is all (X,Z) where (X,Y) in R and ((X,Z) in S otherwise Z in T) and (X,Z) in U;
  
  compTples has type ((string,integer),(string,integer)) =>boolean;
  compTples((A1,N1),(A2,N2)) where A1<A2 is true;
  compTples((A1,N1),(A2,N2)) where A1=A2 and N1<N2 is true;
  compTples(_,_) default is false;
  
  SS is all (X,Z) where (X,Z) in R order by (X,Z) using compTples;
  
  main has type action();
  main() do {
    assert size(SS)=size(R);
    
    logMsg(info,"`all (X,Y) where  (X,Y) in R and (X,Y) in S' is $DD");
    logMsg(info,"QQ is $QQ");
    logMsg(info,"SS is $SS");
  };
}