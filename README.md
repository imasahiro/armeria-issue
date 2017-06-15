https://github.com/line/armeria/issues/614


Step 1. Launch app.
```sh
./gradlew bootRun
```

Step 2. Creates blocking request(s)
```sh
./create_stuck_requests.sh
```

Step 3. Makes other requests
Run ./src/test/java/com/linecorp/armeria/sample/SampleTest.java
