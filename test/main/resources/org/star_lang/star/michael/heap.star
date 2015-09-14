heap is package {

type Heap of %e is E
               or T(integer, %e, Heap of %e, Heap of %e);

heapEmpty has type Heap of %e
heapEmpty is E;

f1 is heapEmpty;

}