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

import java.nio.ByteBuffer
import java.util.concurrent.{ExecutionException, Future}

import akka.actor.Actor
import com.amazon.firehose.data.UserMessage
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseAsyncClient
import com.amazonaws.services.kinesisfirehose.model.{PutRecordRequest, PutRecordResult, Record}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

class FireHoseActor extends Actor with akka.actor.ActorLogging {

  lazy val firehoseClient = createFireHoseClient
  val streamName = sys.env.getOrElse("STREAM_NAME", "firehose_stream")
  implicit val formats = DefaultFormats

  def createFireHoseClient(): AmazonKinesisFirehoseAsyncClient = {
    log.debug("Connect to Firehose Stream: " + streamName)
    val client = new AmazonKinesisFirehoseAsyncClient
    val currentRegion = if (Regions.getCurrentRegion != null) Regions.getCurrentRegion else Region.getRegion(Regions.EU_WEST_1)

    client.withRegion(currentRegion)

    return client
  }

  override def receive = {
    case UserMessage(userId, msg, timestamp) =>
      val userMessage = UserMessage(userId, msg, timestamp)
      val jsonString = write(userMessage)
      log.debug("Sending payload: " +jsonString)
      val payload = ByteBuffer.wrap(jsonString.getBytes)
      sendMessageToFirehose(payload, "partitionkey")
    case _ => log.info("Received a message != UserMessage")
  }

  def sendMessageToFirehose(payload: ByteBuffer, partitionKey: String): Unit = {
    val putRecordRequest: PutRecordRequest = new PutRecordRequest
    putRecordRequest.setDeliveryStreamName(streamName)
    val record: Record = new Record
    record.setData(payload)
    putRecordRequest.setRecord(record)

    val futureResult: Future[PutRecordResult] = firehoseClient.putRecordAsync(putRecordRequest)
    try {
      val recordResult: PutRecordResult = futureResult.get
      log.debug("Sent message to Kinesis Firehose: " + recordResult.toString)
    }
    catch {
      case iexc: InterruptedException => {
        log.error(iexc.getMessage)
      }
      case eexc: ExecutionException => {
        log.error(eexc.getMessage)
      }
    }
  }

}
