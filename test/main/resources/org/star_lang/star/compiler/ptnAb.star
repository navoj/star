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
ptnAb is package{
  -- test the pattern abstraction stuff
  
  positive has type (integer) <= integer;
  positive(I) from (I where I>0);
  
  filter has type (list of %t, (%s)<=%t) => list of %s;
  filter(L,P) is let{
    flt(list{}) is list{};
    flt(list{P(I);..More}) is list{I;..flt(More)};
    flt(list{_;..More}) default is flt(More);
  } in flt(L);
  
  lee(X) from (("lee",X));
  
  main() do
  {
    LL is list{1;-2;34;-1;-2;10;0;-1};
    
    logMsg(info,"filter of $LL is $(filter(LL,positive))");
    
    assert filter(LL,positive)=list{1;34;10};
    
    MM is list{("lee",1); ("lee",2); ("lea",3); ("bar",01); ("lee",3)};
    logMsg(info,"filter of $MM is $(filter(MM,lee))");
    
    assert filter(MM,lee)=list{1;2;3};
  }
}