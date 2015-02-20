name := """phantom_crud"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
	"org.reactivemongo" %  "play2-reactivemongo_2.11" % "0.10.5.0.akka23",
	"org.reactivemongo" %  "reactivemongo-bson_2.11"  % "0.10.5.0.akka23",
  jdbc,
  anorm,
  cache,
  ws
)
