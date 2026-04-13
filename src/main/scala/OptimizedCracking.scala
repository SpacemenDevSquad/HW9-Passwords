import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

val numAvailableCores: Int = Runtime.getRuntime.availableProcessors() - 1
val executorService: ExecutorService = Executors.newFixedThreadPool(numAvailableCores)
implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(executorService)

val startString = "a"
val hashHashMap: Set[String] = getHashes
val charList: List[String] = getCharSet
val charMax: Int = charList.size - 1
val futureArray = new Array[Future[(String, String)]](numAvailableCores)

@main def startUp(): Unit = {
  var passwordStart = startString
  for i <- futureArray.indices do {
    val currStart = passwordStart
    futureArray(i) = Future{checkingPasswords(currStart)}
    passwordStart = incrementString(passwordStart, 1)
  }
  mainThread()
}

def mainThread(): Unit = {
  val indicesRange = futureArray.indices
  while (true) {
    for i <- indicesRange do {
      if (futureArray(i).isCompleted) {
        val result = Await.result(futureArray(i), Duration.Inf)
        println(result)
        futureArray(i) = Future{checkingPasswords(incrementString(result._1, numAvailableCores))}
      }
    }
  }
}

def checkingPasswords(startString: String): (String, String) = {
  @tailrec
  def helper(checkString: String): (String, String) = {
    val matchedString: String = findMatch(sha256(checkString))
    if matchedString == "" then helper(incrementString(checkString, numAvailableCores)) else (checkString, matchedString)
  }
  helper(startString)
}

def incrementString(str: String, n: Int): String = {
  if str.isEmpty then return charList(n)
  val currNum: Int = charList.indexOf(str(str.length-1).toString) + n
  if currNum > charMax then incrementString(str.dropRight(1), currNum/charList.length)+charList(currNum % charList.length) else str.dropRight(1)+charList(currNum)
}

def findMatch(hash: String): String = if hashHashMap.contains(hash) then hash else ""

// loads a set of password hashes from the provided hashes.txt file
def sha256(text: String): String = {
  MessageDigest.getInstance("SHA-256")
    .digest(text.getBytes("UTF-8"))
    .map("%02x".format(_)).mkString.toUpperCase
}

def getHashes: Set[String] = {
  val src = io.Source.fromResource("hashes.txt")
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
  val symbols: String = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
  val fullCharset = lowercase + uppercase + digits + symbols
  fullCharset.split("").toList
}