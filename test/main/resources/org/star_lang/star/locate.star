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
demo is package{
  -- sample programs to check out the match compiler with
  
  type List of %t is nil or pair(%t, List of %t);
  
  
  mappairs has type (((%s,%t) =>%u),List of %s,List of %t) =>List of %u;
  mappairs(F,nil,_) is nil;
  mappairs(F,pair(H,T),nil) is nil;
  mappairs(F,pair(H,T),pair(A,B)) is pair(F(H,A),mappairs(F,T,B));
  
  demo2 has type  (List of %s,List of %s) =>List of %s;
  demo2(nil,Y) is Y;
  demo2(XS,nil) is XS;
  demo2(pair(A,B),pair(X,Y)) is pair(A,pair(X,demo2(B,Y)));
 
  type tree of %t is empty or node{left has type tree of %t; label has type %t; right has type tree of %t};
  
  locate has type [tree of %t, %t] where %t requires comparable =>boolean;
  locate(node{label=Lb},K) where Lb=K is true;
  locate(node{left=L;label=Lb},K) where Lb>K is locate(L,K);
  locate(node{label=Lb;right=R}, K) where Lb<K is locate(R,K);
  locate(_,_) default is false;
  
  chCode has type (integer) =>char;
  chCode(10) is `\n;
  chCode(32) is ` ;
  chCode(48) is `0;
  chCode(X) default is `q;
 
  append has type (list of %t,list of %t) =>list of %t;
  append([],X) is X;
  append([A,..X],T) is [A,..append(X,T)];

  pick has type [list of %t,integer] => %t;
  pick([X,.._],0) is X;
  pick([_,X,.._],1) is X;
  pick([_,_,X,.._],2) is X;
  pick([_,_,_,..R],N) is pick(R,N-3);
  
} 