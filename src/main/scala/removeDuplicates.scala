import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import java.io.{FileWriter, PrintWriter}
import scala.io.Source


@main def removeDupes(): Unit = {
  val src = Source.fromResource("crackedPasswords.txt")
  val crackedSet: Set[String] = src.getLines().toSet
  overrideFile("src/main/resources/sortedPasswords.txt")
  for i <- crackedSet do writeToFile("src/main/resources/sortedPasswords.txt", i)
  src.close()
}

def overrideFile(fileName: String): Unit = {
  val writer = new PrintWriter(fileName)
  try writer.write("")
  finally writer.close()
}