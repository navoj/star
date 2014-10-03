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
binaryTrees is package{
  -- implementation of the binary trees benchmark from shootout.alioth.debian.org
  -- adapted from the YAP prolog version http://shootout.alioth.debian.org/gp4/benchmark.php?test=binarytrees&lang=yap&id=1
  import bitstring;
  
  minDepth is 4;
  
  type tree of %t is tree(tree of %t,%t,tree of %t)
                  or nilTree;
  
  checkTree has type (tree of integer) =>integer;
  checkTree(tree(nilTree,Item,_)) is Item;
  checkTree(tree(_,Item,nilTree)) is Item;
  checkTree(tree(L,I,R)) is I+checkTree(L)-checkTree(R);
  
  buildTree has type (integer,integer) =>tree of integer;
  buildTree(I,0) is tree(nilTree,I,nilTree);
  buildTree(I,D) is tree(buildTree((2*I)-1,D-1),I,buildTree(2*I,D-1));
  
  sho(X,Y) is valof{
    logMsg(info,X);
    valis Y
  };
  
  main() do
  {
    N is 16;
    
    maxDepth is (minDepth+2>N?minDepth|N);
    stretchDepth is maxDepth+1;
        
    longLivedTree is buildTree(0,maxDepth);
    
    -- logMsg(info,"iota $minDepth, $maxDepth =$(iota(minDepth,maxDepth+1,2))");
    
    Start is nanos();
    
    for depth in iota(minDepth,maxDepth+1,2) do{
      iterations is 1.<<.(maxDepth-depth+minDepth);
      
      -- logMsg(info,"iterations= $iterations, depth=$depth, maxDepth=$maxDepth, minDepth=$minDepth");
      
      
      var check := 0;
      for i in iota(1,iterations,1) do{
        check := check+checkTree(buildTree(i,depth));
        check := check+checkTree(buildTree(-i,depth));
      }
      
      -- assert 2*iterations=-check;
    }
    
    Time is nanos()-Start;
    
    logMsg(info,"long lived tree of depth $maxDepth\t check: $(checkTree(longLivedTree)), in $Time nanos");
  }
}