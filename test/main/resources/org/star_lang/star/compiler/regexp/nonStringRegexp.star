nonString is package{
  fun testFn(`(.:C).*`) is some(C)
   |  testFn(_) default is none;
  
  prc main() do {
    assert testFn(nonString) = none
  }
}