Exchage.io
===================
#### by srpingboot 2.1

1. Run with Gradle (default profile 'local')
-------------------
```
# Windows
gradlew.bat clean build && gradlew.bat exchage-web:bootRun

# Other (Linux, macOs)
./gradlew clean build && ./gradlew :exchange-web:bootRun
```

2. Run with Executable java
-------------------
```
./gradlew stage
cd 
java -jar -Dsping.profiles.active=local ./build/libs/exchange-web-0.0.1-SNAPSHOT.jar
```

3. You can then access
-------------------
http://127.0.0.1:8080