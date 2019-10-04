---
layout: home

---

# log4cats-mtl - Log4cats MTL Integration Layer [![Build Status](https://travis-ci.com/ChristopherDavenport/log4cats-mtl.svg?branch=master)](https://travis-ci.com/ChristopherDavenport/log4cats-mtl) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/log4cats-mtl_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/log4cats-mtl_2.12)

## Quick Start

To use log4cats-mtl in an existing SBT project with Scala 2.11 or a later version, add the following dependencies to your
`build.sbt` depending on your needs:

```scala
libraryDependencies ++= Seq(
  "io.chrisdavenport" %% "log4cats-mtl" % "<version>"
)
```

## Examples

```scala mdoc
import io.chrisdavenport.log4cats.extras.LogMessage
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.chrisdavenport.log4cats.mtl._
import cats._
import cats.data._
import cats.effect.Sync
import cats.implicits._
import cats.mtl._
import cats.mtl.implicits._

final case class TraceId(value: String)

implicit val traceIdCtxEncoder: CtxEncoder[TraceId] = (traceId: TraceId) => Map("traceId" -> traceId.value)

def doApplicativeLocalThings[F[_]: Sync: ApplicativeLocal[?[_], TraceId]]: F[Unit] = 
  (for {
    logger <- ApplicativeAskLogger.create[F, TraceId](Slf4jLogger.create[F])
    _ <- logger.info("Logging at start of safelyDoThings").scope(TraceId("inner-id"))
    something <- Sync[F].delay(println("I could do anything"))
      .onError{case e => logger.error(e)("Something Went Wrong in safelyDoThings")}
    _ <- logger.info("Logging at end of safelyDoThings")
  } yield something).scope(TraceId("outter-id"))
  
def doFunctorTellThings[F[_]: Sync: FunctorTell[?[_], Chain[LogMessage]]]: F[Unit] = {
  val logger = FunctorTellLogger[F, Chain]()

  for {
    _ <- logger.info("Logging at start of safelyDoThings")
    something <- Sync[F].delay(println("I could do anything"))
      .onError{case e => logger.error(e)("Something Went Wrong in safelyDoThings")}
    _ <- logger.info("Logging at end of safelyDoThings")
  } yield something
}

```