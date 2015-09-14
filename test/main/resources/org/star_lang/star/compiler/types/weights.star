weights is package {
 type vertex is alias of integer;
 type weight is alias of integer;
 type intmap of t is alias of dictionary of (integer, t);
 type graph is alias of intmap of intmap of weight;

 weight has type (graph, vertex, vertex) => option of weight;
 fun weight(g, i, j) where g[i] has value gI is gI[j]
  |  weight(_,_,_) default is none
}