anydeflt is package{

  type tool is toolData{
    name has type string;
    value has type float
  }
  
  def gold is list of [ toolData{name="alpha"; value=1.0},
                    toolData{name="beta"; value = 2.0} ];
  
  fun getValue(N) is any of V where R in gold and R matches toolData{name=N; value=V};

  prc main() do {
    assert getValue("alpha") has value 1.0;
    
    assert getValue("beta") has value 2.0;
    
    assert getValue("gamma") = none;
    
    def N is "alpha";
    def goldValue is any of V where R in gold and R matches toolData{name=N; value=V};
    assert goldValue has value 1.0;
  }
}
