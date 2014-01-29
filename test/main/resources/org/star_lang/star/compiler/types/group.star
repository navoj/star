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
group is package{
  type group is group{
    elem has kind type where pPrint over elem;
    
    zero has type elem;
    inv has type (elem)=>elem;
    op has type (elem,elem)=>elem;
    
    el has type for all %t such that (%t)=>elem;
  };
  
  modGroup(K) is group{
    type integer counts as elem;
    zero is 0;
    
    inv(X) is K-X;
    
    op(X,Y) is (X+Y)%K;
    
    el(X) is X cast elem;
    
    pPrint\#elem is pPrint\#integer;
  };
  
  double has type (group)=>group;
  double(G) is group{
    type G.elem counts as elem;
    
    zero is G.zero;
    
    inv(X) is G.inv(X);
    
    op(X,Y) is G.op(G.op(X,X),G.op(Y,Y));
    
    el(X) is G.el(X);
    
    pPrint\#elem is G.pPrint\#elem
  }
  
  invGroup(G) is group{
    type G.elem counts as elem;
    
    zero is G.zero;
    
    inv is G.inv;
    
    op(X,Y) is G.inv(G.op(G.inv(X),G.inv(Y)));
    
    el is G.el
    
    pPrint\#elem is G.pPrint\#elem
  }
  
  main() do {
    K is modGroup(7);
    logMsg(info,"K.zero=#(__display(K.zero))");
    logMsg(info,"3+5=#(__display(K.op(K.el(3),K.el(5))))");
    
    D is double(K);
    logMsg(info,"D.zero = #(__display(D.zero))");
    logMsg(info,"3+5=#(__display(D.op(D.el(3),D.el(5))))");
  }
}