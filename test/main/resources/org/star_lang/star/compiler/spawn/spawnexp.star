spawnexp is package{
  prc main() do {
    def x is background task{
      valis "1";        
    };
    def ret is valof x;
    
    assert ret="1"
  }
}