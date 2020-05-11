FROM openjdk:8-jre-alpine
RUN apk --no-cache add curl
# packages needed for opencv
RUN apk --no-cache add gcompat
RUN apk --no-cache add libdc1394-dev
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]