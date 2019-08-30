lazy val baseName = "BDB-JE"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "7.5.11"

lazy val basicJavaOpts = Seq("-source", "1.8")

lazy val commonSettings = Seq(
  version            := projectVersion,
  organization       := "de.sciss",	// used for maven artifact publishing, hence not `com.sleepycat`
  scalaVersion       := "2.12.9",
  javacOptions                   := basicJavaOpts ++ Seq("-encoding", "utf8", "-Xlint:unchecked", "-target", "1.8", "-g"),
  javacOptions in (Compile, doc) := basicJavaOpts,  // doesn't eat `-encoding` or `target`
  homepage           := Some(url(s"https://git.iem.at/sciss/$baseNameL")),
  licenses           := Seq("Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
) ++ publishSettings

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false }
)

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(commonSettings)
  .settings(
    autoScalaLibrary := false,
    crossPaths       := false,
    description      := "Berkeley DB Java Edition is a open source, transactional storage solution for Java applications.",
    javacOptions in Compile ++= Seq("-g", "-target", "1.8", "-source", "1.8"),  // -encoding, utf8
    javacOptions in (Compile, doc) := Nil,  // target/source doesn't work for doc
    javaSource in Compile := baseDirectory.value / "src",
    unmanagedSourceDirectories in Compile := (javaSource in Compile).value :: Nil,
    mainClass in run := Some("com.sleepycat.je.utilint.JarMain"),
    excludeFilter in unmanagedSources := {
      val sleepyDir = baseDirectory.value / "src" / "com" / "sleepycat"
      val jeDir     = sleepyDir / "je"

      // is this a joke -- there no such thing built into sbt?
      case class InSubDir(d: File) extends FileFilter {
        override def accept(f: File): Boolean =
          Option(f.getParentFile) match {
            case Some(`d`)    => true
            case Some(other)  => accept(other)
            case _            => false
          }
      }

      case class Exact(f0: File) extends FileFilter {
        override def accept(f: File): Boolean = f == f0
      }

      case class Name(s: String) extends FileFilter {
        override def accept(f: File): Boolean = f.name == s
      }

      InSubDir(jeDir / "jca") || InSubDir(jeDir / "jmx") || InSubDir(jeDir / "rep" / "jmx") ||
        Exact(sleepyDir / "persist" / "model" / "ClassEnhancerTask.java") || Name("package.html")
    },
    pomExtra := pomExtraBoth,
  )

def pomExtraBoth = pomBase ++ pomDevs

def pomBase = { val n = baseNameL
  <scm>
    <url>git@git.iem.at:sciss/{n}.git</url>
    <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
  </scm>
}

// this is silly: sonatype doesn't validate pom
// without developer entry, and oracle is too
// proud to name the developers
def pomDevSeltzer =
  <developer>
    <id>margoseltzer</id>
    <name>Margo Seltzer</name>
    <url>https://github.com/margoseltzer</url>
  </developer>

def pomDevBostic =
  <developer>
    <id>keithbostic</id>
    <name>Keith Bostic</name>
    <url>https://github.com/keithbostic</url>
  </developer>

def pomDevs =
  <developers>
    {pomDevSeltzer}
    {pomDevBostic}
  </developers>

