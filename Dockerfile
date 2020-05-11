FROM openjdk:14-jdk-alpine
RUN apk --no-cache add curl
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
ARG LIB_FILE=build/libs/libopencv_java412.so
COPY ${JAR_FILE} app.jar
COPY ${LIB_FILE} /usr/lib/libopencv_java412.so
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]