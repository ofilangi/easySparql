//scalaVersion:="2.12.7"

addSbtPlugin("org.jetbrains.scala"% "sbt-ide-settings"              % "1.1.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.13.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.2.0")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"                 % "2.0.7")
addSbtPlugin("com.github.sbt"     % "sbt-release"                   % "1.1.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"           % "0.21.1")
addSbtPlugin("io.crashbox"        % "sbt-gpg"                       % "0.2.2")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"                  % "2.1.0")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
