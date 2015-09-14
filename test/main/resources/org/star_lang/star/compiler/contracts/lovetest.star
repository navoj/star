love is package {
  type Love of %c is Everywhere or Only(%c);

  def herLove is Everywhere;

  contract Sailor over %c is {
    sail has type (%c) => Love of %c;
  };

  loveIsAllAround has type (Love of %c) => boolean where Sailor over %c;
  fun loveIsAllAround(Everywhere) is true
   |  loveIsAllAround(_) default is false

  prc main() do {
    logMsg(info, "$(loveIsAllAround(herLove))");
  }
}