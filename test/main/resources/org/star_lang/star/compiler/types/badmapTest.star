badmapTest is package{
  fooPattern has type () <= string;
  def fooPattern is (() from (S matching `.*foo.*`));

  Y has type dictionary of ((string, () <= string));
  def Y is dictionary of [
        "foo" -> fooPattern
        ]

  prc main() do { logMsg(info, "Y is $Y"); };
}