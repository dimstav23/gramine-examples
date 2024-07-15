# GO example

This directory contains a Makefile and a manifest template for running a simple
GO application that performs sample image processing operations using the 
[bild](https://github.com/anthonynsimon/bild/) framework in Gramine.

# Building

## Building for Linux

Run `make` (non-debug) or `make DEBUG=1` (debug) in the directory.

## Building for SGX

Run `make SGX=1` (non-debug) or `make SGX=1 DEBUG=1` (debug) in the directory.

# Run the GO example with Gramine

Without SGX:
```sh
gramine-direct bild_bench
```

With SGX:
```sh
gramine-sgx bild_bench
```

