import java.security.MessageDigest
import java.util.concurrent.{ExecutorService, Executors}
import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.collection.{AbstractIterable, AbstractIterator, mutable}
import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import java.io.{FileWriter, PrintWriter}

val fileList: List[String] = getFileContents("gitLab10Mil.txt").toList

@main def fileCrackStartUp(): Unit = {
  for i <- futureArray.indices do {
      futureArray(i) = Future{fileCrackPassword(i)}
  }
  fileMainThread()
}

def fileMainThread(): Unit = {
  val indicesRange = futureArray.indices
  while (true) {
    for i <- indicesRange do {
      if (futureArray(i).isCompleted) {
        val result = Await.result(futureArray(i), Duration.Inf)
        if result._1.toInt != -1 then {
          futureArray(i) = Future {
            fileCrackPassword(result._1.toInt + numAvailableCores)
          }
          val crackPass = fileList(result._1.toInt)
          val finalString = "(" + crackPass + ", " + result._2 + ")"
          writeToFile("src/main/resources/crackedPasswords.txt", finalString)
          println("Found Password: "+crackPass)
        }
      }
    }
  }
}

def fileCrackPassword(startIndex: Int): (String, String) = {
  @tailrec
  def helper(n: Int): (String, String) = {
    if (n >= fileList.length) return ("-1", "")
    val matchedString: String = findMatch(sha256(fileList(n)))
    if matchedString == "" then helper(n+numAvailableCores) else (n.toString, matchedString)
  }
  helper(startIndex)
}

def getFileContents(fileName: String): Seq[String] = {
  val src = io.Source.fromResource(fileName)
  try {
    src.getLines().toSeq
  } finally {
    src.close()
  }
}

def writeToFile(fileName: String, message: String): Unit = {
  val writer = new FileWriter(fileName, true)
  try writer.write(message+"\n")
  finally writer.close()
}