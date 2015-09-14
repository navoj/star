matchingQuery is package{
  type employee is empl{
    name has type string;
    id has type integer;
    ssn has type string;
  }
  
  def Joe is empl{ name="joe"; id=0; ssn="001-01-0001"};
  def Peter is empl{ name="peter"; id=1; ssn="002-02-0002"};
  def John is empl{ name="john";id=2;ssn="003-03-0003"};
  
  def E is list of [ Joe,  Peter, John ];
                  
  fun findEmpl(N,I,S,EE) is 
    list of { all Em where Em matching empl{ name = Nm; id = ID; ssn = SSN} in EE and
                           (N has value NN implies NN=Nm) and (I has value II implies ID=II) and (S has value SS implies SS=SSN)};
     
  prc main() do {
    assert findEmpl(some("joe"),none,none,E) = list of [ Joe ];
    assert findEmpl(none,some(1),none,E) = list of [Peter];
    assert findEmpl(none,some(4),none,E) = list of []
  }
}