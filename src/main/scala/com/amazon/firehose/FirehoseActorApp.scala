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

package com.amazon.firehose

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.amazon.firehose.data.UserMessage
import org.scalatra.{Ok, ScalatraServlet}

import scala.concurrent.ExecutionContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._


class FirehoseActorApp(system: ActorSystem, firehoseActor: ActorRef) extends ScalatraServlet with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  implicit val timeout = new Timeout(2, TimeUnit.SECONDS)

  protected implicit def executor: ExecutionContext = system.dispatcher

  post("/user") {
    val userMessage = parsedBody.extract[UserMessage]
    firehoseActor ! userMessage
    Ok()
  }

  get("/healthcheck") {
    Ok()
  }
}


