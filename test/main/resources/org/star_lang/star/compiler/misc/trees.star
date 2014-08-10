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
trees is package{
  -- The trees package tests out positional constructors and general recursion
 
  type tree of t is 
      nul
   or labeled(tree of t, t, tree of t);
   
  type weekday is monday or tuesday or wednesday or thursday or friday or saturday or sunday;
  
  leaves has type (tree of %t) => list of %t;
  leaves(nul) is list of [];
  leaves(labeled(L,Lb,R)) is leaves(L)++list of [Lb,..leaves(R)];
  
  insert has type (tree of %t, %t ) => tree of %t where comparable over %t;
  insert(nul,T) is labeled(nul,T,nul);
  insert(labeled(L,B,R),T) where B>T is labeled(insert(L,T),B,R);
  insert(labeled(L,B,R),T) where B<T is labeled(L,B,insert(R,T));
  
  rotate has type (tree of %t) => tree of %t;
  rotate(nul) is nul;
  
  locate(nul,_) is false;
  locate(labeled(_,Lb,_),Lb) is true;
  locate(labeled(L,Lb,_),LL) where LL<Lb is locate(L,LL);
  locate(labeled(_,Lb,R),LL) where LL>Lb is locate(R,LL);
  
  find has type (list of ((%s,%t)),%s ) =>option of %t where equality over %s;
  find(_,_) default is none;
  find(list of [(K,V),.._],Ky) where K=Ky is some(V);
  find(list of [_,..R],Ky) is find(R,Ky);
  
  main() do
  {
    T is insert(insert(insert(insert(nul,"alpha"),"gamma"),"beta"),"delta");
    logMsg(info,"T is $T");
    
    assert locate(T,"alpha");
    assert locate(T,"delta");
    assert not locate(T,"eta");
    
    assert find(list of {("alpha",1);("beta",2)},"beta") = some(2)
    assert find(list of {("alpha",1);("beta",2)},"gamma") = none
  };
}