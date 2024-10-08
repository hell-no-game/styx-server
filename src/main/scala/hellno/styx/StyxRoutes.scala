package hellno.styx

import cats.effect.Sync
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object StyxRoutes:

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F]:
      case GET -> Root / "joke" =>
        J.get >>= (Ok(_))

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F]:
      case GET -> Root / "hello" / name =>
        H.hello(HelloWorld.Name(name)) >>= (Ok(_))
