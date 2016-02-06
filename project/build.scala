/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import sbtassembly.AssemblyPlugin.autoImport._

object AkkafirehoseBuild extends Build {
  val Organization = "com.amazon"
  val Name = "akka-firehose"
  val Version = "0.1.0"
  val ScalaVersion = "2.11.7"
  val ScalatraVersion = "2.3.0"
  val Json4sVersion = "3.2.9"

  lazy val project = Project (
    "akka-firehose",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      scalacOptions += "-target:jvm-1.7",
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.10.v20150310" % "container;compile",
        "com.typesafe.akka" %% "akka-actor" % "2.3.4",
        "com.amazonaws" % "aws-java-sdk-kinesis" % "1.10.32",
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s"   %% "json4s-jackson" % Json4sVersion,
        "org.json4s" %% "json4s-native" % Json4sVersion,
        "org.json4s" %% "json4s-ext" % Json4sVersion,
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  ).settings(mainClass in assembly := Some("JettyLauncher"))
}
