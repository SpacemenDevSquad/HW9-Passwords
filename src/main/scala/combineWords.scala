import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import java.io.{FileWriter, PrintWriter}


var combineWordsStartAt = 0

@main def combineWords(): Unit = {
  val passwords: List[String] = getFileContents("words.txt").toList
  for i <- 0 until numAvailableCores do {
    combineWordsCreateFuture(i, combineWordsStartAt)
    combineWordsStartAt += 1
  }

  combineWordsMainThread()
}


def combineWordsMainThread(): Unit = {
  while true do {
    for i <- futureArray.indices do {
      if (futureArray(i).isCompleted) {
        val result = Await.result(futureArray(i), Duration.Inf)
        if (result != null) println(result)
        combineWordsCreateFuture(i, combineWordsStartAt)
        combineWordsStartAt += 1
      }
    }
  }
}

def combineWordsCreateFuture(index: Int, i: Int): Unit = {
  futureArray(index) = Future {
    var answer: (String, String) = null
    for j <- passwords do {
      val isMatch = findMatch(sha256(passwords(i)+j))
      if (isMatch != "") answer = (passwords(i)+j,isMatch)
    }
    answer
  }
}

