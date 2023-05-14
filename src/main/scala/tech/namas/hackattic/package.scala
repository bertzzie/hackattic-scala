package tech.namas.hackattic

import sttp.client4.*
import sttp.model.{HeaderNames, MediaType}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

private val asyncBackend = DefaultFutureBackend()

private val resultField = "result"

def accessToken(): String = System.getenv("HACKATTIC_ACCESS_TOKEN") match {
  case null  => "" // let it fail
  case value => value
}

def fetch[T](url: String, bodyConverter: ResponseAs[Either[String, T]], parser: T => String): Future[String] =
  quickRequest
    .get(uri"${url}?access_token=${accessToken()}")
    .response(bodyConverter)
    .send(asyncBackend) map {
      case response if response.isSuccess => response.body match {
        case Right(content) => parser(content)
        case Left(error)    => s"failed to read response body: ${error}"
      }

      case response =>
        s"Sending GET request to ${url} failed with status code ${response.code.code} and body ${response.body}"
    }

def post(url: String, body: ujson.Obj): Future[String] =
  quickRequest
    .post(uri"${url}?access_token=${accessToken()}")
    .header(HeaderNames.ContentType, MediaType.ApplicationJson.toString)
    .body(ujson.write(body))
    .send(asyncBackend) map {
      case response if response.isSuccess =>
        ujson.read(response.body)(resultField).toString

      case response =>
        s"Sending POST request to ${url} failed with status code ${response.code.code} and body ${response.body}"
    }
