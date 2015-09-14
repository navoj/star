clashingNames is package {
  import actors;
  
  fun foo() is actor {
    on x on bar do nothing;
  };
  def bar is actor{
    on x on DEFAULT do{
      def y is foo();
    } 
  };    
}