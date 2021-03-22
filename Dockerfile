FROM docker.io/java:8
WORKDIR /app
ADD ./target/videomanagement-0.0.1-SNAPSHOT.jar ./service.jar
CMD java -jar service.jar