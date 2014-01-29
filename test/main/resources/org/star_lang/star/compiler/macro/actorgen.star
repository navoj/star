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
actorgen is package{
  
  #genActor(?XX) ==> actor{
    #*genRules(?XX);
    var foo := 10;
  };
  
  #genRules(#(?L;?R)#) ==> #(genRules(L);genRules(R))#;
  #genRules(KK(?E,?C,?A)) ==> on E on C do A;
  
  gen is genActor(#(KK(A,C,logMsg(info,"Got $A on C"));KK(B,D,logMsg(info,"hello")))#);
  
  main() do {
    notify gen with 1 on C;
    notify gen with 2 on D;
  }
}
  