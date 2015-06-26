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
mapseq is package{
  -- test out type inference over sequences
  
  contract mmap over %%s is {
    mapseq has type for all %e,%f such that (%%s of %e,(%e)=>%f) => %%s of %f
  }
  
  implementation mmap over list is {
    fun mapseq(S, F) is valof{
      var alreadyMapped := [];
	  var toBeMapped := S;
	  while toBeMapped matches [X,.. restTBM] do {
	    alreadyMapped := [alreadyMapped..,F(X)];
	    toBeMapped := restTBM;
	  };
	  valis alreadyMapped;
    };
  }
  
  prc main() do {
    def XX is mapseq(list of [1,2,3], ((X) => X*2));
    
    logMsg(info,"XX=$XX");
    
    assert XX = list of [2,4,6];
  }
}  
    