# image-search
Spring boot app for image searching

### Building Gradle


### Building Docker image
`./gradlew build && java -jar build/libs/app.jar`

`curl https://drive.google.com/file/d/1qV4X9Dl0gXQDb865cqIHlVnYYLCSEj4b/view?usp=sharing --output build/libs/libopencv_java412.so`

`docker build --build-arg JAR_FILE=build/libs/*.jar --build-arg LIB_FILE=build/libs/*.so -t steel8rat/image-search .`

