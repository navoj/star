splay is package{
  import priority;
  
  splayHeap has type for all t such that (ordering of t)=>priorityQ of t where pPrint over t and equality over t;
  fun splayHeap(Element) is priorityQ{
    type heap is E or T(heap,t,heap);
    
    def emptyQ is E
    
    fun isEmptyQ(E) is true
     |  isEmptyQ(_) default is false
    
    private
    fun partition(pivot,E) is (E,E)
     |  partition(pivot,t matching T(a,x,b)) is 
          Element.le(x,pivot) ?
	        (switch b in {
	          case E is (t,E);
	          case T(b1,y,b2) is 
	            Element.le(y,pivot) ? valof{
		          def (small,big) is partition(pivot,b2);
		          valis (T(T(a,x,b1),y,small),big)
	            } : 
	            valof{
		          def (small,big) is partition(pivot,b1);
		          valis (T(a,x,small),T(big,y,b2));
	            }
	        })
      : (switch a in {
	      case E is (E,t);
	      case T(a1,y,a2) is 
	        Element.le(y,pivot) ?
	          valof{
		        def (small,big) is partition(pivot,a2);
		        valis (T(a1,y,small),T(big,x,b))
	          } :
	          valof{
		        def (small,big) is partition(pivot,a1);
		        valis (small,T(big,y,T(a2,x,b)));
	          }
	    })
	    
    fun insertQ(x,t) is valof{
      def (a,b) is partition(x,t);
      valis T(a,x,b);
    };

    fun mergeQ(E,t) is t
     |  mergeQ(T(a,x,b),t) is valof{
          def (ta,tb) is partition(x,t);
          valis T(mergeQ(ta,a),x,mergeQ(tb,b));
        };

    fun firstEl(E) is raise "empty"
     |  firstEl(T(E,x,_)) is x
     |  firstEl(T(a,x,b)) is firstEl(a)
    
    fun restQ(E) is raise "empty"
     |  restQ(T(E,x,b)) is b
     |  restQ(T(T(E,x,b),y,c)) is T(b,y,c)
     |  restQ(T(T(a,x,b),y,c)) is T(restQ(a),x,T(b,y,c))
  }
}