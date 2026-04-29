


val attemptWord = "art3"

@main def tryAWord(): Unit = {
  val isMatch = findMatch(sha256(attemptWord))
  if isMatch == "" then println("No match found")
  else print("("+attemptWord+","+isMatch+")")
}