# Kotlin Concurrent Word Counter

A file processing application that demonstrates the use of Kotlin Coroutines and Channels for parallel word counting.

## Overview

This application parses large text files and counts the occurrences of words. For increased performance
it uses Kotlin Coroutines with channels.

## How to Build

```bash
./gradlew build
```
## How to Run
```bash
./gradlew run
```
## In IntelliJ IDEA Ultimate
1. Open the project in IntelliJ IDEA Ultimate.
2. Go to `Run` > `Edit Configurations`.
3. Add a new `Application` configuration.
4. Set the `Main class` to `Main`.
5. Set the `Program arguments` to your desired command line arguments.
6. Apply the changes and run the configuration.

## Command line arguments
- `-f`: Path to the input text file.
- `-t`: Number of coroutines.
- `--generate-file`: Generate a sample text file for testing.
- `-n`: Number of lines to generate in the sample file (used with `--generate-file`).

## Example Usage
```bash
./gradlew run --args="-f file.txt -t 10 --generate-file -n 3000000"```
