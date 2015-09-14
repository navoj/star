funproc is package {
  fun writeMsg(msg) is
    logMsg(info, display(msg))

  prc main () do {
    writeMsg("msg");
  }
}