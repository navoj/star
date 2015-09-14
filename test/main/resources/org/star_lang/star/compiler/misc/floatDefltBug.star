floatDefltBug is package{

  type result is result {
    name has type string;
    value_A has type float;
    value_B has type float;
    group has type string;
    diff has type float;
    diff default is (value_B-value_A);
    L_spec has type float;
    H_spec has type float;
  };
  
  def testValue is 1.0;
  def goldValue is 1.5;
  
  prc main() do {
    def N is "fred";
    def groupName is "group";
    def lower is 0.0;
    def upper is 1.0;
     
    def R is result{
      name=N;
      value_A=goldValue;
      value_B=testValue;
      group=groupName;
      L_spec=lower;
      H_spec=upper;
      };
    assert R.name=N;
    assert R.diff = testValue-goldValue;
  }
}