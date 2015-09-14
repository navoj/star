stringfind is package{
  -- test string find
  
  def TT is "the lazy dog jumped over the quick brown fox";
  
  prc main() do {
    assert findstring(TT,"the",0)=0;
    assert findstring(TT,"over",0)=20;
    assert findstring(TT,"the",5)=25;
    assert findstring(TT,"fax",0)=-1;
  }
}