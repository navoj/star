worksheet{
  var S := set of [1,2,3,4,5,6,7,8]

  assert S = set of [1,2,3,4,5,6,7,8]

  perform update (X where X%2=0) in S with X*X

  show S

  assert S = set of [1,4,3,16,5,36,7,64]
}