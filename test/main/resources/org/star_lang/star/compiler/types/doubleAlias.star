doubleAlias is package{
  type NewsEvent is NewsEvent{
    time has type double;
    symbol has type string;
    sentiment has type long;
    news has type string;
  }
  
  prc main() do {
    def N is NewsEvent{
      def time is 6.5;
      def symbol is "AAA";
      def sentiment is nonLong;
      def news is "no news is good news"
    };
    
    assert N.time=6.5;
  }
}