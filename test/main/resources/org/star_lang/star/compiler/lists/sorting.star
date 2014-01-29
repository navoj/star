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
-- An example involving defining types and some algorithmic computation

sorting is package{
  
  msort has type (list of %t ) => list of %t where comparable over %t;
  msort(list{X}) is list{X};
  msort(list{}) is list{};
  msort(list{pivot;..L})  is let{
    split(list{},Lf,R) is (Lf,R);
    split(list{E;..more},Lf,R) where E<pivot is split(more,list{E;..Lf},R);
    split(list{E;..more},Lf,R) default is split(more,Lf,list{E;..R});
    
    (LL,RR) is split(L,list{},list{});
   } in msort(LL)<>list{pivot;..msort(RR)}
   
  L1 is list{1;5;2;0};
  L1S is list{0;1;2;5};
   
  main() do {
    logMsg(info,"sort of [1,5,2,0] is $(msort(L1))");
    
    assert msort(L1)=L1S;
  }
}
