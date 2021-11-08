FROM hseeberger/scala-sbt:8u265_1.4.3_2.13.4
ADD src/ /build/StocksProducer/src/
ADD project/ /build/StocksProducer/project/
ADD build.sbt /build/StocksProducer/
WORKDIR /build/StocksProducer/
RUN sbt assembly

FROM openjdk:8u265-jre-slim
COPY --from=0 /build/StocksProducer/target/scala-2.13/stocks-producer.jar /app/
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "stocks-producer.jar"]
