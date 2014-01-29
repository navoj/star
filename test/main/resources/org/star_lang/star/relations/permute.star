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
permute is package{
  permute(list of {}) is list of {};
  permute(list of {E}) is list of {list of {E}};
  permute(list of {E;..Mr}) is foldIn(E,permute(Mr));
  
  foldIn(E,list of {}) is list of {};
  foldIn(E,list of {Tpl;..Tpls}) is shuffle(E,Tpl,list of {}) ++ foldIn(E,Tpls);
    
  shuffle(E,list of {},Pre) is list of {list of {Pre..;E}};
  shuffle(E,list of {H;..T},Pre) is list of { Pre++list of {E;H;..T};.. shuffle(E,T,list of {H;..Pre})}  
  
  NofM(0,_) is list of {};
  NofM(1,L) is list of { (list of {E}) where E in L};
  NofM(K,list of {E1;..Mr}) where K>0 is glue(E1,NofM(K-1,Mr))++NofM(K,Mr);
  NofM(K,list of {}) is list of {};
  
  private glue(E,L) is _map(L,(function(X) is list of {E;..X}));
  
  multicat has type (list of list of %t)=>list of %t;
  multicat(L) is rightFold(_concat,list of {},L);
}