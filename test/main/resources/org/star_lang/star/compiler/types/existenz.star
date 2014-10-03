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
existenz is package{
  type grp is alias of { elem has kind type where pPrint over elem; op has type (elem,elem)=>elem };
  
  type xrec of %t is nox or xrec{ elem has kind type; val has type (elem)=>%t };
 
  grp has type grp;
  grp is { 
    type integer counts as elem;
    op has type (integer,integer)=>integer;
    op(X,Y) is X+Y;
  }
  
  GF has type grp;
  GF is {
    type elem = float;
    op = (+);
  };
  
  XX is xrec{ type integer counts as elem; val(I) is I };
  
  YY has type {elem has kind type where pPrint over elem; op has type (elem,elem)=>elem; pp has type (elem)=>elem };
  YY is {
    open grp;
    
    -- open xrec{ type string counts as elem; val("") is 3}
    pp(X) is op(X,X);
  }
    
  main() do {
    logMsg(info,"grp=$grp");
    
    Y1 is YY.pp(2 cast YY.elem);
    logMsg(info,"Y1=$Y1");
    
    logMsg(info,"G1 = $(GF.op(1.0 cast GF.elem,2.0 cast GF.elem))");
  }
}
