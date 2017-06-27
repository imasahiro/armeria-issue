https://github.com/line/armeria/issues/116


Step 1. Launch app.
```sh
./gradlew bootRun
```

Step 2. Check sync and async endpoint
```sh
curl -v http://localhost:8080/async
curl -v http://localhost:8080/sync
```
