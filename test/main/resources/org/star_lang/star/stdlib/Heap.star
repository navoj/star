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
/*
 * Leftist heaps from Okasaki
 */


/* doesn't work:
Heap has type ()=>package{
type Heap;
heapEmpty has type Heap of %e;
heapIsEmpty has type (Heap of %e) => boolean;
heapMerge has type (Heap of %e, Heap of %e) => Heap of %e;
heapInsert has type (%e, Heap of %e) => Heap of %e;
heapFindMin has type (Heap of %e) => %e;
heapDeleteMin has type (Heap of %e) => Heap of %e;
}
*/

Heap is package {
import Prelude

type Heap of %e is E
		  		or T(integer, %e, Heap of %e, Heap of %e);

-- heapEmpty has type Heap of %e
heapEmpty is E

-- f1 has type Heap of integer;
-- f1 is heapEmpty

heapIsEmpty has type (Heap of %e) => boolean
heapIsEmpty(E) is true
heapIsEmpty(_) default is false

heapMerge has type (Heap of %e, Heap of %e) => Heap of %e;
heapMerge(h, E) is h
heapMerge(E, h) is h
heapMerge(h1 matching T(_, x, a1, b1), h2 matching T(_, y, a2, b2)) is
 	((x <= y) ? makeT(x, a1, heapMerge(b1, h2)) | makeT(y, a2, heapMerge(h1, b2)))

rank has type (Heap of %e) => integer 
rank(E) is 0;
rank(T(r, _, _, _)) is r;

makeT has type (%e, Heap of %e, Heap of %e) => Heap of %e; 
makeT(x, a, b) is
	((rank(a) >= rank(b)) ? T(rank(b) + 1, x, a, b) | T(rank(a) + 1, x, b, a))
	
heapInsert has type (%e, Heap of %e) => Heap of %e
heapInsert(x, h) is heapMerge(T(1, x, E, E), h)

heapFindMin has type (Heap of %e) => %e
heapFindMin(T(_, x, _, _)) is x

heapDeleteMin has type (Heap of %e) => Heap of %e
heapDeleteMin(T(_, _, a, b)) is heapMerge(a, b)

-- #### the type definition triggers a bug in the Star compiler 	
-- heapUnorderedEnumerator has type (Heap of %val) => CollEnumerator of (%val, %seed)
heapUnorderedEnumerator(h) is
	(function(iteratee, seed) is
		let {
			var acc := seed
			traverse(h) do {
				logMsg(info, "traverse " ++ display(h));
				case h in {
					E do nothing
					T(_, x, h1, h2) do {
						case iteratee(acc, x) in {
							Left(last) do acc := last;
							Right(new) do {
								logMsg(info, "traversing " ++ display(h) ++ " " ++ display(acc) ++ " " ++ display(new));
								acc := new;
								logMsg(info, "h1 = " ++ display(h1));
								traverse(h1);
								logMsg(info, "h2 = " ++ display(h2));
								traverse(h2);
							}				
						}
						
					}
				}
			}
		} in
			valof { traverse(h); valis acc; })
	
-- #### the type definition triggers a bug in the Star compiler 	
-- heapOrderedEnumerator has type (Heap of %val) => CollEnumerator of (%val, %seed);
heapOrderedEnumerator(heap) is
	(function (iteratee, seed) is
		valof {
			resource var acc := seed;
			resource var h := heap;
			loop::
			while true do {
				if h = E
				then valis acc
				else {
					m is heapFindMin(h);
					case iteratee(acc, m) in {
						Left(last) do valis last
						Right(new) do {
							acc := new;
							h := heapDeleteMin(h)
						}
					}					
				}
			}
		})

}