logicalTest is package{

  prc main() do
  {	
	def t is true;
	def f is false;
	
	assert t or f;
	assert f or t;
    assert t or t;
    assert t and (t or f);
    assert t and (f or t);
	assert t and (t or t);
	assert not ( t and (f or f));
  };
}