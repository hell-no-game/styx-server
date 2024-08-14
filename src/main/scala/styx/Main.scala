package styx

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = StyxServer.run[IO]
