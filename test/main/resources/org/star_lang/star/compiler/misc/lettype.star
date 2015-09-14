lettype is package{

  prc test() do let{
    type testType is One or Two;
  } in {
    logMsg(info, "Tested");
  };

  prc main() do {
    logMsg(info, "Testing ...");
    test();
  };
}
