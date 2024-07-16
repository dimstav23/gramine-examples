# Java image processing example

This directory contains a Makefile and a manifest template for running a simple
Java application that performs sample image processing operations in Gramine.

# Building

## Building for Linux

Run `make` (non-debug) or `make DEBUG=1` (debug) in the directory.

## Building for SGX

Run `make SGX=1` (non-debug) or `make SGX=1 DEBUG=1` (debug) in the directory.

# Run the Java image processing example with Gramine

Without SGX:
```sh
gramine-direct java ImageProcessing input.png
```

With SGX:
```sh
gramine-sgx java ImageProcessing input.png
```
