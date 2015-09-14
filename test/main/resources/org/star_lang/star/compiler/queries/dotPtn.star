dotPtn is package{
  type address is address{
    country has type string;
  }
  
  def countries is list of ["usa", "uk", "japan"];
  
  fun includeStore(X) is (X.country) in countries;
  
  prc main() do {
    assert includeStore(address{country="usa"});
    assert includeStore(address{country="japan"});
    
    assert not includeStore(address{country="canada"});
  }
}
