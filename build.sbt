import sbtcrossproject.CrossPlugin.autoImport.crossProject

val log4catsV = "1.0.0"
val catsMtlV = "0.7.0"
val catsEffectV = "2.0.0"

val kindProjectorV = "0.11.0"
val betterMonadicForV = "0.3.1"


// Projects
lazy val `log4cats-mtl` = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(commonSettings)

lazy val mtlJVM = `log4cats-mtl`.jvm
lazy val mtlJS  = `log4cats-mtl`.js

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings)
  .dependsOn(mtlJVM)
  .settings{
    import microsites._
    Seq(
      micrositeName := "log4cats-mtl",
      micrositeDescription := "Log4cats MTL Integration Layer",
      micrositeAuthor := "Christopher Davenport",
      micrositeGithubOwner := "ChristopherDavenport",
      micrositeGithubRepo := "log4cats-mtl",
      micrositeBaseUrl := "/log4cats-mtl",
      micrositeDocumentationUrl := "https://www.javadoc.io/doc/io.chrisdavenport/log4cats-mtl_2.12",
      micrositeGitterChannelUrl := "ChristopherDavenport/libraries", // Feel Free to Set To Something Else
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      scalacOptions in Tut --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-numeric-widen",
        "-Ywarn-dead-code",
        "-Ywarn-unused:imports",
        "-Xlint:-missing-interpolator,_"
      ),
      libraryDependencies ++= Seq(
        "org.typelevel"     %% "cats-effect"    % catsEffectV,
        "io.chrisdavenport" %% "log4cats-slf4j" % log4catsV
      ),
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
          file("CODE_OF_CONDUCT.md")  -> ExtraMdFileConfig("code-of-conduct.md",   "page", Map("title" -> "code of conduct",   "section" -> "code of conduct",   "position" -> "100")),
          file("LICENSE")             -> ExtraMdFileConfig("license.md",   "page", Map("title" -> "license",   "section" -> "license",   "position" -> "101"))
      )
    )
  }

// General Settings
lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.10"),

  addCompilerPlugin("org.typelevel" %  "kind-projector"     % kindProjectorV cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % betterMonadicForV),

  libraryDependencies ++= Seq(
    "io.chrisdavenport" %% "log4cats-core" % log4catsV,
    "org.typelevel"     %% "cats-mtl-core" % catsMtlV
  )
)

// General Settings
inThisBuild(List(
  organization := "io.chrisdavenport",
  developers := List(
    Developer("ChristopherDavenport", "Christopher Davenport", "chris@christopherdavenport.tech", url("https://github.com/ChristopherDavenport"))
  ),

  homepage := Some(url("https://github.com/ChristopherDavenport/log4cats-mtl")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),

  pomIncludeRepository := { _ => false},
  scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
      "-doc-source-url", "https://github.com/ChristopherDavenport/log4cats-mtl/blob/v" + version.value + "€{FILE_PATH}.scala"
  )
))