binaryTrees is package{
  -- implementation of the binary trees benchmark from shootout.alioth.debian.org
  -- adapted from the YAP prolog version http://shootout.alioth.debian.org/gp4/benchmark.php?test=binarytrees&lang=yap&id=1
  import bitstring;
  
  def minDepth is 4;
  
  type tree of %t is tree(tree of %t,%t,tree of %t)
                  or nilTree;
  
  checkTree has type (tree of integer) =>integer
  fun checkTree(tree(nilTree,Item,_)) is Item
   |  checkTree(tree(_,Item,nilTree)) is Item
   |  checkTree(tree(L,I,R)) is I+checkTree(L)-checkTree(R)
  
  buildTree has type (integer,integer) =>tree of integer
  fun buildTree(I,0) is tree(nilTree,I,nilTree)
   |  buildTree(I,D) is tree(buildTree((2*I)-1,D-1),I,buildTree(2*I,D-1))
  
  fun sho(X,Y) is valof{
    logMsg(info,X);
    valis Y
  };

  main has type ()=>()
  prc main() do
  {
    def N is 16;
    
    def maxDepth is (minDepth+2>N?minDepth:N);
    def stretchDepth is maxDepth+1;
        
    def longLivedTree is buildTree(0,maxDepth);
    
    -- logMsg(info,"iota $minDepth, $maxDepth =$(iota(minDepth,maxDepth+1,2))");
    
    def Start is nanos();
    
    for depth in iota(minDepth,maxDepth+1,2) do{
      def iterations is 1.<<.(maxDepth-depth+minDepth);
      
      -- logMsg(info,"iterations= $iterations, depth=$depth, maxDepth=$maxDepth, minDepth=$minDepth");
      
      
      var check := 0;
      for i in iota(1,iterations,1) do{
        check := check+checkTree(buildTree(i,depth));
        check := check+checkTree(buildTree(-i,depth));
      }
      
      -- assert 2*iterations=-check;
    }
    
    def Time is nanos()-Start;
    
    logMsg(info,"long lived tree of depth $maxDepth\t check: $(checkTree(longLivedTree)), in $Time nanos");
  }
}