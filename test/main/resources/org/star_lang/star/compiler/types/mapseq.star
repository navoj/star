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
mapseq is package{
  -- test out type inference over sequences
  
  contract mmap over %%s is {
    mapseq has type for all %e,%f such that (%%s of %e,(%e)=>%f) => %%s of %f
  }
  
  implementation mmap over array is {
    mapseq(S, F) is valof{
      var alreadyMapped := sequence of {};
	  var toBeMapped := S;
	  while toBeMapped matches sequence of {X;.. restTBM} do {
	    alreadyMapped := sequence of {alreadyMapped..;F(X)};
	    toBeMapped := restTBM;
	  };
	  valis alreadyMapped;
    };
  }
  
  main() do {
    XX is mapseq(list of {1;2;3}, (function(X) is X*2));
    
    logMsg(info,"XX=$XX");
    
    assert XX = list of {2;4;6};
  }
}  
    