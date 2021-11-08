name := "StocksProducer"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "3.0.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0",
)

assemblyJarName in assembly := "stocks-producer.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
