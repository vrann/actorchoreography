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
#COPY --from=build /app/l11/target/l11-1.0-SNAPSHOT.jar /app/target/l11-1.0-SNAPSHOT.jar
RUN apt-get update && apt-get install -y openssh-server
RUN mkdir /var/run/sshd
RUN echo 'root:THEPASSWORDYOUCREATED' | chpasswd
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
# SSH login fix. Otherwise user is kicked off after login
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd



COPY topology/target/topology-1.0-SNAPSHOT.jar /app/target/topology-1.0-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/target/topology-1.0-SNAPSHOT.jar"]
CMD ["--help"]