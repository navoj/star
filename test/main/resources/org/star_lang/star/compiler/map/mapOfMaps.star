mapOfMaps is package{
  MM has type ref dictionary of (string, ref dictionary of (string,integer));
  var MM := dictionary of [];
  
  prc main() do {
    MM["a"] := _cell(dictionary of ["b"->0]);
    MM["b"] := _cell(dictionary of [])
    
    -- (!MM["a"])["b"] := 1;
    def M is someValue(MM["a"]);
    M["b"] := 1;
    
    assert (!someValue(MM["a"]))["b"] has value 1;
    
    def N is someValue(MM["b"]);
    N["c"] := 1;
    -- (!MM["b"])["c"] := 1;
    
    assert (!someValue(MM["b"]))["c"] has value 1;
  }
}