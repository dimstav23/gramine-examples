# Copyright (C) 2024 Gramine contributors
# SPDX-License-Identifier: BSD-3-Clause

ARCH_LIBDIR ?= /lib/$(shell $(CC) -dumpmachine)

JMH_DIR = jmh
JMH_SHA256 ?= d6bc79d4b49c975573f7771c92e2b8ce93117e695a8b937bc9ab70f5d8c14867
JMH_SRC ?= 1.37.tar.gz
JMH_MIRRORS ?= https://github.com/openjdk/jmh/archive/refs/tags

JAVA_HOME ?= /usr/

ifeq ($(DEBUG),1)
GRAMINE_LOG_LEVEL = debug
else
GRAMINE_LOG_LEVEL = error
endif

.PHONY: all
all: build java.manifest
ifeq ($(SGX),1)
all: java.manifest.sgx java.sig
endif

fetch_jmh:
	../common_tools/download --output $(JMH_SRC) --sha256 $(JMH_SHA256) \
		$(foreach mirror,$(JMH_MIRRORS),--url $(mirror)/$(JMH_SRC))
	mkdir -p $(JMH_DIR)
	tar -C $(JMH_DIR) --strip-components=1 -xf $(JMH_SRC)

prepare: fetch_jmh
	if ! [ -d $(JMH_DIR)/test ]; then \
		cd $(JMH_DIR) && mvn archetype:generate \
			-DinteractiveMode=false \
			-DarchetypeGroupId=org.openjdk.jmh \
			-DarchetypeArtifactId=jmh-java-benchmark-archetype \
			-DgroupId=org.sample \
			-DartifactId=test \
			-Dversion=1.0; \
	fi

build: prepare
	cd $(JMH_DIR)/test/ && JAVA_HOME=$(JAVA_HOME) mvn clean verify

java.manifest: java.manifest.template
	gramine-manifest \
		-Dlog_level=$(GRAMINE_LOG_LEVEL) \
		-Darch_libdir=$(ARCH_LIBDIR) \
		-Dentrypoint=$(realpath $(shell sh -c "command -v java")) \
		$< >$@

# Make on Ubuntu <= 20.04 doesn't support "Rules with Grouped Targets" (`&:`),
# for details on this workaround see
# https://github.com/gramineproject/gramine/blob/e8735ea06c/CI-Examples/helloworld/Makefile
java.manifest.sgx java.sig: sgx_sign
	@:

.INTERMEDIATE: sgx_sign
sgx_sign: java.manifest
	gramine-sgx-sign \
		--manifest $< \
		--output $<.sgx

.PHONY: clean
clean:
	$(RM) *.token *.sig *.manifest.sgx *.manifest $(JMH_DIR)/test/target/benchmarks.jar

.PHONY: distclean
distclean: clean
	$(RM) -r $(JMH_DIR) *.tar.gz