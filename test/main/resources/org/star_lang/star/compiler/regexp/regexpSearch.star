regexpSearch is package{

  def fooPattern is ( () from (S matching `.*foo.*`));

  subjectPatterns has type list of ((string, () <= string));
  def subjectPatterns is list of [
    ("foo", fooPattern)
  ];

  def testResult is all D where (D, _) in subjectPatterns;
  
  prc main() do {
    logMsg(info,"$testResult");
    assert testResult=list of ["foo"];
  }
}