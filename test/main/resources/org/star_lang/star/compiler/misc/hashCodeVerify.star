hashCodeVerify is package{
  # #(dbg(?msg, ?A))# ==> logMsg(info, msg ++ " = " ++ __display(?A));

  prc main() do {
    dbg("foo", __hashCode(3.0));
  }
}