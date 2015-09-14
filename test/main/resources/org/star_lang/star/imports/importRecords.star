importRecords is package{
  NP is import NP;

  def R is NP.RC{id="fred"}

  prc main() do {
    assert R.id="fred";
  }
}