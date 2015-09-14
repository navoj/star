macrotest is package{
  -- testing new macro architecture
  
  import macro;
  
  prc main() do {
    logMsg(info,"macro key of $(quote(alpha)) is $(macroKey(quote(alpha)))");
    logMsg(info,"macro key of $(quote(quote(alpha))) is $(macroKey(quote(quote(alpha))))");
    logMsg(info,"macro key of $(quote((quote(alpha)))) is $(macroKey(quote((quote(alpha)))))");
  }
}