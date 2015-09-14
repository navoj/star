evenPrimes is package{
  isEvenAndPrime has type (integer) => boolean;
  fun isEvenAndPrime(2) is true
   |  isEvenAndPrime(_) default is false

  isEvenAndPrime3 has type boolean;
  def isEvenAndPrime3 is isEvenAndPrime(3);
  
  prc main() do {
    logMsg(info,"$isEvenAndPrime3");
    assert not isEvenAndPrime3;
  }
}