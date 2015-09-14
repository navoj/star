common is package{
  private var X := 0;
  
  fun commonX() is X;
  
  fun commonUpdate(XX) is valof{
    X := XX;
    valis X
  }
}