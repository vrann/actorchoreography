#FROM java:8-jdk AS build
#
#RUN apt-get update -y && apt-get install maven -y
#COPY l11 /app/l11
#COPY l11dialogue /app/l11dialogue
#COPY pom.xml /app
#WORKDIR /app
#RUN mvn package
#RUN ls -la l11dialogue/target

# This results in a single layer image
FROM java:8-jre
#COPY --from=build /app/l11dialogue/target/l11dialogue-1.0-SNAPSHOT.jar /app/target/l11dialogue-1.0-SNAPSHOT.jar
COPY l11dialogue/target/l11dialogue-1.0-SNAPSHOT.jar /app/target/l11dialogue-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/target/l11dialogue-1.0-SNAPSHOT.jar"]
CMD ["--help"]