
condRegexp is package {
  prc main () do {
    def msg is ( "hello, world" matches `(.*), ` 
    ? "matches" 
    : "does not match");
    logMsg(info, msg);
    assert msg="does not match";
  }
}