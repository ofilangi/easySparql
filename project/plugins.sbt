//scalaVersion:="2.12.7"

addSbtPlugin("org.jetbrains.scala"% "sbt-ide-settings"              % "1.1.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.10.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "1.1.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.1.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.3")
//addSbtPlugin("com.vmunier"        % "sbt-web-scalajs"               % "1.2.0")
//addSbtPlugin("com.typesafe.play"  % "sbt-plugin"                    % "2.8.8")
//addSbtPlugin("com.typesafe.sbt"   % "sbt-digest"                    % "1.1.4")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"                 % "1.9.3")
addSbtPlugin("com.github.sbt"     % "sbt-release"                   % "1.1.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalajs-bundler"           % "0.20.0")
addSbtPlugin("io.crashbox"        % "sbt-gpg"                       % "0.2.1")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"                  % "1.1.0")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
