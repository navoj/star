worksheet{

  fun mentionsFruit(C) is C matches `(.*apple.*|.*orange.*)`;
  
  fun mentionsFruit2(C) is C matches `.*apple.*` or C matches `.*orange.*`;
  
  ptn matchFruit() from `(.*apple.*|.*orange.*)`;
  
  def content is "A Granny Smith is an apple and is green." 
  
  assert mentionsFruit(content);
  assert mentionsFruit2(content);
    
  assert content matches matchFruit();
}