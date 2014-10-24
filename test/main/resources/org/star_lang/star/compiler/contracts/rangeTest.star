worksheet{
  show range of [0,1,2]
  assert range of [0,1,2] = range(0,3,1)
  
  assert range(0.0,3.0,1.0) matches [0.0,1.0,2.0]
  
  assert range(3.0,0.0,-1.0) matches [3.0,2.0,1.0]
} 