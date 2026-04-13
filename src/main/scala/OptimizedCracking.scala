import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}

val numCores: Int = Runtime.getRuntime.availableProcessors() - 1
val executorService: ExecutorService = Executors.newFixedThreadPool(numCores)
implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)

val hashHashMap: Set[String] = null
val charList: List[String] = null

@main def startUp(): Unit = {
  println(io.Source.fromResource("hashes.txt"))
  //print(charList)
}

def mainThread(): Unit = {

}

def checkingPasswords(startString: String): String = {
  @tailrec
  def helper(checkString: String): String = {
    val matchedString: String = findMatch(sha256(checkString))
    if matchedString == "" then helper(incrementString(checkString, numCores)) else matchedString
  }
  helper(startString)
}

def incrementString(str: String, n: Int): String = {
  ""
}

def findMatch(hash: String): String = if hashHashMap.contains(hash) then hash else ""

def sha256(text: String): String = {
  MessageDigest.getInstance("SHA-256")
    .digest(text.getBytes("UTF-8"))
    .map("%02x".format(_)).mkString.toUpperCase
}

// loads a set of password hashes from the provided hashes.txt file
def getHashes: Set[String] = {
  val src = io.Source.fromResource("hashes.txt")
  print("Hey\n")
  try {
    src.getLines().toSet
  } finally {
    src.close()
  }
}

def getCharSet: List[String] = {
  val digits = "0123456789"
  val lowercase = "abcdefghijklmnopqrstuvwxyz"
  val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  val symbols = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
  val fullCharset = lowercase + uppercase + digits + symbols
  "".split(fullCharset).toList
}