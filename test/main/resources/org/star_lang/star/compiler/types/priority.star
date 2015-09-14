priority is package{
  type priorityQ of t is priorityQ{
    heap has kind type where pPrint over heap;
    emptyQ has type heap;
    isEmptyQ has type (heap)=>boolean;
    insertQ has type (t,heap)=>heap;
    mergeQ has type (heap,heap)=>heap;
    firstEl has type (heap)=>t;
    restQ has type (heap)=>heap;
  }
  
  type ordering of t is ordering{
    lt has type (t,t)=>boolean;
    le has type (t,t)=>boolean;
    eq has type (t,t)=>boolean;
    ge has type (t,t)=>boolean;
    gt has type (t,t)=>boolean;
  }
}