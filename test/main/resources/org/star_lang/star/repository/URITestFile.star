URITestFile is package {
  def x is 42;
  prc main() do {
	logMsg(info, "test", "URITestFile");
	assert x = 42;
  }
}