# Visitor File System

A Java 17 in-memory file system that uses the Visitor pattern to calculate total size, search files by suffix and minimum size, and report file count, directory count, and maximum depth.

Run from the folder containing `pom.xml` with JDK 17 and Maven installed.

**Run the tests**
```sh
mvn test
```

**Run the demo**
```sh
mvn compile
java -cp target/classes filesystem.Main
```
