deepRecord is package{
  -- Test deeper access to records
  
  type outer is outer{
    name has type string;
    inner has type inner;
  };
  
  type inner is inner{
    val has type integer;
  }
  
  prc main() do {
    def O is outer{ name="fred"; inner=inner{val=10}};
    
    assert O.name="fred";
    assert O.inner.val=10;
    
    def R is list of [ O];
    
    def XX is any of X.name where X in R and X.inner.val=10;
    assert XX has value "fred"
  }
}