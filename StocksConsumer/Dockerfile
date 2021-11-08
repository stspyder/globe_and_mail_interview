FROM hseeberger/scala-sbt:8u265_1.4.3_2.13.4
ADD src/ /build/StocksConsumer/src/
ADD project/ /build/StocksConsumer/project/
ADD build.sbt /build/StocksConsumer/
WORKDIR /build/StocksConsumer/
RUN sbt assembly

FROM openjdk:8u265-jre-slim
COPY --from=0 /build/StocksConsumer/target/scala-2.13/stocks-consumer.jar /app/
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "stocks-consumer.jar"]
