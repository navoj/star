arrayIndexMatch is package{
  def A is list of [ "alpha", "beta", "gamma", "delta" ];
  
  def B is cons of [ "alpha", "beta", "gamma", "delta" ];
  
  ptn consIndex(L[Ix]) from (L,Ix) where Ix>=0 and Ix<size(L);
  
  prc main() do {
  	assert (A,0) matches __array_index(E);
  	
  	if (A,0) matches __array_index(E) then
  	  logMsg(info,E);
  	  
  	assert (A,0) matches __array_index(E) and E matches "alpha";
  	
  	assert not (A,4) matches __array_index(E);
  	
  	assert (B,1) matches consIndex(some("beta"));
  	
  	assert not (B,4) matches consIndex(_);
  }
}