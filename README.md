# globe_and_mail_interview

Running `docker-compose up` would bring up the following 4 containers. Please read below about what the containers are and any steps that you may have to perform before you bring them up.

## Kafka Server
This is a public confluent image for Kafka. Nothing special is going on here. Within network binds to 9092, within the container binds at 29092. Also exposes out to port 29092 on the host.

## Zookeeper Server
This is a public confluent image for Zookeeper. Binds to port 2181. Exposes out to port 22181 to host.

## Producer
The docker file for this container is at `StocksProducer/Dockerfile`. This is a multistage build. The first stage builds a fat jar for the producer using scala/sbt image. The second stage actually builds the container image using openjdk-jre image. Because this image both builds the jar and the container, you do not have to run separate `sbt` commands.

## Consumer
The docker file for this container is at `StocksConsumer/Dockerfile`. Same as producer. Builds a fat jar and builds the container image using openjdk-jre image. However, because spark is a dependency to the consumer, it might take a while to build this fat jar.

Before bring up this image, please make sure that your docker deamon is allowed to use at least 5 GB from the system. Spark needs atleast 450MB to start up and not having enough memory will cause the container to abort. `mem_reservation: 512m` has been set for this container for this reason.


At any point, if you wish to rebuild the image after changing the source, delete the existing image using `docker rmi <image id>` and then use `docker-compose up --build` to build the image and bring the containers up.

## Console Output
The assigment says to print the result of consumer to console (which I have did). To look at the results of the consumer aggregation, please use `docker ps` to get the consumer container name / id. Then use `docker logs <consumer container id> -f 2>/dev/null` to get only the stdout where the aggregation results are printed.
