defltTest is package{
  positive has type (integer) <= integer;
  ptn positive(N) from N where N > 0;
	
  firstNChars has type (integer, string) => string;
  fun firstNChars(positive(N), S) where S matches`(.:C)(.*:restS)` is C ++ firstNChars(N - 1, restS)
   |  firstNChars(_, _) default is "";

  prc main() do let {
    def fstNCh is firstNChars(10, "abcdefghi");
  } in logMsg(info, "fstNCh is $fstNCh"); 
    
}
