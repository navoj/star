applyQuote is package{

  splitApply(<| #(?N)# #@ ?Args |>) is (N,Args)
  
  main() do {
    (O,A) is splitApply(<| alpha + omega |>)
    logMsg(info,"O=$O, A=$A")
  }
}
   