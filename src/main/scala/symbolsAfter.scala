import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


var startAt = 0

val passwords: List[String] = getFileContents("words.txt").toList
val digits = "0123456789"
val lowercase = "abcdefghijklmnopqrstuvwxyz"
val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
val symbols = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
val symbolsTry: Seq[String] = (digits+uppercase+symbols).split("").toList

@main def symbolAfterWord(): Unit = {
  for i <- 0 until numAvailableCores do {
    createFuture(i, startAt)
    startAt += 1
  }
  symbolMainThread()
}

def createFuture(index: Int, i: Int): Unit = {
  futureArray(index) = Future {
    var answer: (String, String) = null
    for j <- symbolsTry.indices do {
      for k <- symbolsTry.indices do {
        val attempt = passwords(i) + symbolsTry(j) + symbolsTry(k)
        val isMatch = findMatch(sha256(attempt))
        if (isMatch != "") answer = (attempt, isMatch)
      }
      val attempt2 = passwords(i) + symbolsTry(j)
      val isMatch2 = findMatch(sha256(attempt2))
      if (isMatch2 != "") answer = (attempt2, isMatch2)
    }
    null
  }
}


def symbolMainThread(): Unit = {
  while true do {
    for i <- futureArray.indices do {
      if (futureArray(i).isCompleted) {
        val result = Await.result(futureArray(i), Duration.Inf)
        if (result != null) println(result)
        createFuture(i, startAt)
        startAt += 1
      }
    }
  }
}