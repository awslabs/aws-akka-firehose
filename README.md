# aws-akka-firehose
This example application implements an Akka actor that writes JSON data into Amazon Kinesis Firehose using the AWS Java SDK.
The application uses embedded Jetty and Scalatra to expose a REST-interface.

## Before you get started

Before running the samples, you'll want to make sure that your environment is
configured to allow the samples to use your
[AWS Security Credentials](http://docs.aws.amazon.com/general/latest/gr/aws-security-credentials.html).

By default the samples use the [DefaultAWSCredentialsProviderChain][DefaultAWSCredentialsProviderChain]
so you'll want to make your credentials available to one of the credentials providers in that
provider chain. There are several ways to do this such as providing a ~/.aws/credentials file,
or if you're running on EC2, you can associate an IAM role with your instance with appropriate
access.

## Building & Running the application

Using the sbt shell, it is possible to use container:start to run the sample:

```sh
$ cd aws-akka-firehose
$ ./sbt
> container:start
```

To build a Fat-JAR containing the application and all dependencies, run `sbt assembly`

## Test the sample application

To test the application, run the following curl-command.

```
curl -v -H "Content-type: application/json" -X POST -d '{"userId":100, "userName": "This is user data"}' http://localhost:8080/api/user
```

## Release Notes
### Release 1.0.0 (Feb 06, 2016)
* Initial commit

[DefaultAWSCredentialsProviderChain]: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html
