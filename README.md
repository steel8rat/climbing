# image-search
Spring boot app for image searching

### Building Gradle


### Building Docker image
`./gradlew build && java -jar build/libs/app.jar`

`docker build --build-arg JAR_FILE=build/libs/*.jar -t steel8rat/image-search .`

