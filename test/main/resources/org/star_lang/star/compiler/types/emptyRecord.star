emptyRecord is package{
  def E is {};

  fun testEmpty({}) is true;

  prc main() do {
    assert testEmpty(E)
  }
}