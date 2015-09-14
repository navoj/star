dateFormat is package{
  prc main() do {
    def D is "3/12/2012 00:00 AM" as date;
    logMsg(info,"$D");
    logMsg(info,"$(__display(D))");
    def DD is parse_date("3/12/2012","MM/dd/yyyy")
    logMsg(info,"$(__display(DD))");
    assert D=DD;
    
    def SS is format_date(D,"MM/dd/yyyy");
    logMsg(info,"SS=$SS");
    assert SS="03/12/2012";
    
    assert "$D:MM/dd/yyy;"="03/12/2012"
    
    logMsg(info,"now: $(now()):MM-dd-yyy;");
  }
}