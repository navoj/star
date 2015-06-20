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
reducible is package{
  private import monad;

  contract reducible over s determines e is {
    _reduce has type
      for all r,m such that
        (s, (e, IterState of r) => m of IterState of r, IterState of r) =>
           m of IterState of r where monad over m;
  };

  implementation reducible over (cons of %e) determines %e is {
    _reduce = consReduce;
  }
  
  private
  fun consReduce(nil, _, state) is return(state)
   |  consReduce(cons(x, xs), reducer, state matching NoMore(_)) is return(state)
   |  consReduce(cons(x, xs), reducer, state matching ContinueWith(_)) is
        bind(reducer(x, state),( (newState) => consReduce(xs, reducer, newState)))
   |  consReduce(cons(x, xs), reducer, state matching NoneFound) is
        bind(reducer(x, state),
	       ( (newState) => consReduce(xs, reducer, newState)));
	       
  fun adder(J,ContinueWith(I)) is some(ContinueWith(I+J))
   |  adder(_,NoMore(I)) is some(NoMore(I))
   |  adder(I,NoneFound) is some(ContinueWith(I))

  prc main() do {
    def II is cons of {1;2;3};

    def Reslt is _reduce(II,adder,NoneFound);
    logMsg(info,"Reslt=$Reslt");
  }
}
    