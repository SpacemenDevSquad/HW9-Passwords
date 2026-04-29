import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import java.io.{FileWriter, PrintWriter}


@main def combineWords(): Unit = {
  val passwords: List[String] = getFileContents("words.txt").toList
  for i <- passwords.indices do {
    Future {
      for j <- passwords.indices do {
        val isMatch = findMatch(sha256(passwords(i)+passwords(j)))
        if (isMatch != "") println("("+passwords(i)+passwords(j)+","+isMatch+")")
      }
    }
  }
}


