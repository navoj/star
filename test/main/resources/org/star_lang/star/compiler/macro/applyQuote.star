applyQuote is package{

  fun splitApply(<| #(?N)# #@ ?Args |>) is (N,Args)
  
  prc main() do {
    def (O,A) is splitApply(<| alpha + omega |>)
    logMsg(info,"O=$O, A=$A")
  }
}
   