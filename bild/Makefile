# Copyright (C) 2024 Gramine contributors
# SPDX-License-Identifier: BSD-3-Clause

ARCH_LIBDIR ?= /lib/$(shell $(CC) -dumpmachine)

# URL of the input image
INPUT_IMAGE_URL="https://upload.wikimedia.org/wikipedia/en/7/7d/Lenna_%28test_image%29.png"
# URL of the watermark image
WATERMARK_IMAGE_URL="https://upload.wikimedia.org/wikipedia/commons/6/64/Intel-logo-2022.png"

ifeq ($(DEBUG),1)
GRAMINE_LOG_LEVEL = debug
else
GRAMINE_LOG_LEVEL = error
endif

.PHONY: all
all: input.png watermark.png bild_bench bild_bench.manifest
ifeq ($(SGX),1)
all: bild_bench.manifest.sgx bild_bench.sig
endif

input.png:
	echo "Downloading input.png"
	wget $(INPUT_IMAGE_URL) -O $@ 2> /dev/null

watermark.png:
	echo "Downloading watermark.png"
	wget $(WATERMARK_IMAGE_URL) -O $@ 2> /dev/null

bild_bench: main.go go.mod go.sum
	go build

bild_bench.manifest: bild_bench.manifest.template
	gramine-manifest \
		-Dlog_level=$(GRAMINE_LOG_LEVEL) \
		$< >$@

# Make on Ubuntu <= 20.04 doesn't support "Rules with Grouped Targets" (`&:`),
# for details on this workaround see
# https://github.com/gramineproject/gramine/blob/e8735ea06c/CI-Examples/helloworld/Makefile
bild_bench.manifest.sgx bild_bench.sig: sgx_sign
	@:

.INTERMEDIATE: sgx_sign
sgx_sign: bild_bench.manifest
	gramine-sgx-sign \
		--manifest $< \
		--output $<.sgx

ifeq ($(SGX),)
GRAMINE = gramine-direct
else
GRAMINE = gramine-sgx
endif

.PHONY: check
check: all
	$(GRAMINE) bild_bench

.PHONY: clean
clean:
	$(RM) *.manifest *.manifest.sgx *.token *.sig bild_bench output.png

.PHONY: distclean
distclean: clean
	$(RM) input.png watermark.png
