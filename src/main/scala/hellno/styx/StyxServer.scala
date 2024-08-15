package hellno.styx

import cats.effect.Async
import cats.effect.kernel.Resource
import cats.syntax.all.*
import com.comcast.ip4s.*
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import org.http4s.server.middleware.Logger

object StyxServer:

  def server[F[_]: Async: Network]: Resource[F, Server] =
    for
      client <- EmberClientBuilder.default[F].build
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg       = Jokes.impl[F](client)

      helloWorldRoutes = StyxRoutes.helloWorldRoutes[F](helloWorldAlg)
      jokeRoutes       = StyxRoutes.jokeRoutes[F](jokeAlg)
      httpApp          = (helloWorldRoutes <+> jokeRoutes).orNotFound

      appWithLogging = Logger.httpApp(true, true)(httpApp)

      server <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(appWithLogging)
          .build
    yield server

  def run[F[_]: Async: Network]: F[Nothing] =
    server.useForever
