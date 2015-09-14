tak is package{
  fun tak(x,y,z) where y<x is tak(tak(x-1,y,z),tak(y-1,z,x),tak(z-1,x,y))
   |  tak(x,y,z) default is y
    
  fun takI(x,y,z) where __integer_lt(y,x) is takI(takI(__integer_minus(x,1_),y,z),takI(__integer_minus(y,1_),z,x),takI(__integer_minus(z,1_),x,y))
   |  takI(x,y,z) default is y;
  
  fun takk(integer(x),integer(y),integer(z)) is integer(takI(x,y,z))
   
  prc main() do {
     def St is nanos();
     def R is tak(18,12,6);
     -- R is takk(19,13,5);
     -- R is tak(19,13,5);
     def Tm is nanos()-St;
     
     logMsg(info,"tak(18,12,6) is $R");
     logMsg(info,"took $(Tm) nanoseconds");
     assert R=18;
  }
}