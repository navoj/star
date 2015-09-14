tplPtns is package{
  fun expect(future, past) is
    (future, past) matches (13, 14);
    
  prc main() do {
     assert expect(13,14);
     assert not expect(12,23);
  }
}