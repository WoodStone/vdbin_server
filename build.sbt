name := "vdbin_server"
 
version := "1.0" 
      
lazy val `vdbin_server` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

//libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

//libraryDependencies += jdbc
//libraryDependencies += evolutions
//libraryDependencies += guice
//libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.20.1"
libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  guice,
  "org.postgresql"  % "postgresql"                   % "9.4-1200+",
  "org.skinny-framework" %% "skinny-orm" % "3.0.0",
  "org.scalikejdbc" %% "scalikejdbc"                  % "3.3.1",
  "org.scalikejdbc" %% "scalikejdbc-config"           % "3.3.1",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.3",
  "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.6.0-scalikejdbc-3.3"
)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

      