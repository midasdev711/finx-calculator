# FROM openjdk:17-alpine
# ARG JAR_FILE=target/*.jar
# COPY ${JAR_FILE} app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]
# EXPOSE 8080

FROM maven:3-openjdk-17 AS build
RUN mkdir /usr/src/project
COPY . /usr/src/project
WORKDIR /usr/src/project
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
RUN mkdir /project
COPY --from=build /usr/src/project/target/Finx-1.0-SNAPSHOT.jar /project/
WORKDIR /project
CMD java -jar Finx-1.0-SNAPSHOT.jar
EXPOSE 8081