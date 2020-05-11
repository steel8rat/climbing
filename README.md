# image-search
Spring boot app for image searching

### Building Gradle


### Building Docker image
`./gradlew build && java -jar build/libs/app.jar`

`curl https://spaces.routesetter.app/misc/libopencv_java412.so --output build/libs/libopencv_java412.so`

`docker build --build-arg JAR_FILE=build/libs/*.jar --build-arg LIB_FILE=build/libs/*.so -t steel8rat/image-search .`

