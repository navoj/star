assertions is package{
  import person;
  
  def people is list of [someone{name="peter"}, someone{name="john"; spouse=noone}, someone{name="fred"}, someone{name="fred"},someone{name="andy"}];

  prc main() do
  {
    assert size(people)=5;
  }
}