package tech.namas.hackattic

import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.decoder.Decoder
import com.google.zxing.{BinaryBitmap, MultiFormatReader}
import sttp.client4.quick.*
import sttp.client4.{DefaultFutureBackend, Response, ResponseAs}
import tech.namas.hackattic.*

import java.io.{File, FileInputStream, FileOutputStream, PrintWriter}
import javax.imageio.ImageIO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

val imageURLField = "image_url"

@main def readingQR(): Unit =
  val executionResult = for {
    imageURL <- fetch(s"${Constants.HackAtticBaseURL}/challenges/reading_qr/problem", asString, getImageURL)
    code     <- fetch(imageURL, asByteArray, decodeQR)
    result   <- post(s"${Constants.HackAtticBaseURL}/challenges/reading_qr/solve", ujson.Obj("code" -> code))
  } yield println(result)

  Await.result(executionResult, Duration.Inf)

def getImageURL(content: String): String =
  ujson.read(content)(imageURLField).str

def decodeQR(content: Array[Byte]): String =
  val imgFile = generateTempFile(content)

  val binaryBitmap = new BinaryBitmap(
    new HybridBinarizer(
      new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(imgFile)))
    )
  )

  val decoder = new MultiFormatReader().decode(binaryBitmap)
  decoder.getText

def generateTempFile(content: Array[Byte]): File =
  val tmpFile = File.createTempFile("qr-decode", "temp")
  val outStream = new FileOutputStream(tmpFile)
  outStream.write(content)
  outStream.close()
  tmpFile.deleteOnExit()

  tmpFile
