A is package {
  import B;

  prc main() do {
	funcB({prc funcA() do logMsg(info,"funcA called")});
	
	assert executedB();
  }
}