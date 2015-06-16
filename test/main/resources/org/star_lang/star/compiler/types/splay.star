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
splay is package{
  import priority;
  
  splayHeap has type for all t such that (ordering of t)=>priorityQ of t where pPrint over t and equality over t;
  splayHeap(Element) is priorityQ{
    type heap is E or T(heap,t,heap);
    
    emptyQ is E;
    
    isEmptyQ(E) is true;
    isEmptyQ(_) default is false;
    
    private
    partition(pivot,E) is (E,E);
    partition(pivot,t matching T(a,x,b)) is 
      Element.le(x,pivot) ?
	(case b in {
	  E is (t,E);
	  T(b1,y,b2) is 
	    Element.le(y,pivot) ? valof{
		def (small,big) is partition(pivot,b2);
		valis (T(T(a,x,b1),y,small),big)
	      } | 
	      valof{
		def (small,big) is partition(pivot,b1);
		valis (T(a,x,small),T(big,y,b2));
	      }
	})
      | (case a in {
	  E is (E,t);
	  T(a1,y,a2) is 
	    Element.le(y,pivot) ?
	      valof{
		def (small,big) is partition(pivot,a2);
		valis (T(a1,y,small),T(big,x,b))
	      } |
	      valof{
		def (small,big) is partition(pivot,a1);
		valis (small,T(big,y,T(a2,x,b)));
	      }
	});
    insertQ(x,t) is valof{
      def (a,b) is partition(x,t);
      valis T(a,x,b);
    };

    mergeQ(E,t) is t;
    mergeQ(T(a,x,b),t) is valof{
      def (ta,tb) is partition(x,t);
      valis T(mergeQ(ta,a),x,mergeQ(tb,b));
    };

    firstEl(E) is raise "empty";
    firstEl(T(E,x,_)) is x;
    firstEl(T(a,x,b)) is firstEl(a);
    
    restQ(E) is raise "empty";
    restQ(T(E,x,b)) is b;
    restQ(T(T(E,x,b),y,c)) is T(b,y,c);
    restQ(T(T(a,x,b),y,c)) is T(restQ(a),x,T(b,y,c));
  }
}