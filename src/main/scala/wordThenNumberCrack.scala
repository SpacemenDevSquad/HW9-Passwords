import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


var wordNumStartAt = 0

@main def wordNumStart(): Unit = {
  for i <- 0 until numAvailableCores do {
    wordNumCreateFuture(i, startAt)
    wordNumStartAt += 1
  }
  wordNumMainThread()
}

def wordNumCreateFuture(index: Int, i: Int): Unit = {
  futureArray(index) = Future {
    val tryPass = passwords(i)
    @tailrec
    def helper(nextNum: Int): (String, String) = {
      val isMatch = findMatch(sha256(tryPass+nextNum.toString))
      if isMatch != "" then (tryPass+nextNum,isMatch)
      else if (tryPass+nextNum.toString).length > 20 then null
      else helper(nextNum+1)
    }
    helper(0)
  }
}


def wordNumMainThread(): Unit = {
  println("Started")
  while true do {
    for i <- futureArray.indices do {
      if (futureArray(i).isCompleted) {
        val result = Await.result(futureArray(i), Duration.Inf)
        if (result != null) println(result)
        wordNumCreateFuture(i, wordNumStartAt)
        wordNumStartAt += 1
      }
    }
  }
}