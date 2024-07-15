# Java [`jmh`](https://github.com/openjdk/jmh) benchmark example

This directory contains an example for running an OpenJDK benchmark
example based on [`jmh`](https://github.com/openjdk/jmh) in Gramine,
including the Makefile and a template for generating the manifest.

## Installing prerequisites

For generating the manifest and running the OpenJDK example, run the
following command to install the required packages (Ubuntu-specific):
```
sudo apt-get install maven openjdk-21-jdk
```

## Building for gramine-direct

Run `make` (non-debug) or `make DEBUG=1` (debug) in the directory.

## Building for gramine-sgx

Run `make SGX=1` (non-debug) or `make SGX=1 DEBUG=1` (debug) in the directory.

## Run OpenJDK benchmark example with Gramine

Here we list an example where we `disable the auto detection` of compiler features to speed-up the 
startup of the benchmark, we limit the VM to use `8G` of heap and run the benchmark with `8` threads.

Without SGX:
```
gramine-direct java -Djmh.blackhole.autoDetect=false -Xmx8G -jar jmh/test/target/benchmarks.jar -t 8
```

With SGX:
```
gramine-sgx java -Djmh.blackhole.autoDetect=false -Xmx8G -jar jmh/test/target/benchmarks.jar -t 8
```

## Dependencies
- `mvn`
- `openjdk`

## Tested versions:
`mvn`:
```
$ mvn --version

Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 21.0.3, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.8.0-1006-intel", arch: "amd64", family: "unix"
```

`jdk`:
```
$ java --version

openjdk 21.0.3 2024-04-16
OpenJDK Runtime Environment (build 21.0.3+9-Ubuntu-1ubuntu1)
OpenJDK 64-Bit Server VM (build 21.0.3+9-Ubuntu-1ubuntu1, mixed mode, sharing)
```