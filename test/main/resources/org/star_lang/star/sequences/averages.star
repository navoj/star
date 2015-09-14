worksheet{
  fun average(C) is leftFold1((+),C) as float/size(C) as float

  show average(list of [1,2,3,4])
}