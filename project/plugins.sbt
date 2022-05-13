//scalaVersion:="2.12.7"

addSbtPlugin("org.jetbrains.scala"% "sbt-ide-settings"              % "1.1.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.10.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.2.0")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"                 % "2.0.0-M4")
addSbtPlugin("com.github.sbt"     % "sbt-release"                   % "1.1.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"           % "0.20.0")
addSbtPlugin("io.crashbox"        % "sbt-gpg"                       % "0.2.1")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"                  % "2.0.0-RC1")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
