-- A test of SR concept graphs

import rdf;

peopleGraph is package{

  def people is graph{
    :john ! :parent $ :sam;
    :john ! :parent $ :jim;
    
    :jane ! :parent $ [:sam, :jim];
    
    :peter ! :parent $ :jj;
    :peter ! :address $ "2 smart place";
    :peter ! :address $ "1 holiday Dr";
    
    :peter ! [ :parent $ :jj, :address $ [ "2 smart place", "1 holiday Dr"]];
    
   -- ( lives $ john ) ! address $ "1 smart Place":"en";
  };
  
  var others := graph{};
  
  prc main() do { -- X is john ! address;
    -- XX is ( john ! address) $ village;
    for Tr in people do
      logMsg(info,display(Tr));
    logMsg(info,display(others));
  }
}   