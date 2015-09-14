fixedExeption is package{
  prc main() do {
    try {
      def errorCode is 99;
      raise "My Exception":errorCode;
    } on abort {
      case exception (errText, errCode, errLocation) do {
                logMsg(info, "Caught Exception\nerrText: $errText, errCode: $errCode, errLocation: $errLocation");                
            }
        }
    };
 }