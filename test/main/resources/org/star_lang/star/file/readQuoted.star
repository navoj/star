readQuoted is package{

  main() do {
    Q is __read_quoted("test:readQuoted.star" as uri);

    logMsg(info,"quoted self is $Q");
  }
}