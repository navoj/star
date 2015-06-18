graphing is package{
  type triple of (c,p) is triple(c,p,c);

  type graph of (c,p) is graph{
    gr has kind type where pPrint over gr and equality over gr;
    
    ar has kind type where arithmetic over ar;

    empty has type ()=>gr;
    addTriple has type (gr,triple of (c,p))=>gr;
  }

  type concept is idea(string) or text(string);

  def relGraph is graph{
    type list of triple of (concept,concept) counts as gr;
    
    type integer counts as ar;

    fun empty() is list of [];

    fun addTriple(R,T) is R union (list of [T]);
  }

  prc main() do {
    def gr0 is relGraph.empty();
    def gr1 is relGraph.addTriple(gr0,triple(idea("c1"),idea("p1"),idea("c2")));

    def gr4 is relGraph.addTriple(gr1,triple(idea("c3"),idea("p1"),idea("c1")));
    def gr5 is relGraph.addTriple(gr4,triple(idea("c3"),idea("p1"),idea("c1")));
    
    logMsg(info,"gr4 is $gr4");
    logMsg(info,"gr5 is $gr5");

    assert (let{ open relGraph } in (=))(gr4,gr5)
  }
}
