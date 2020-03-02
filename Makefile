# This makefile creates a grammlex.jar file for the Grammlex
# java application.
#
# The target "test" requires junit to be installed and you may need
# to set JUNIT_JAR to the jar location. This variable and HAMCREST_JAR
# are set to the location that IntelliJ uses but you may need to
# alter them for your environment.
#
# If you are looking for the rules for the class files, there is a
# make function defined below called define_compile_rules which defines
# class file targets and their dependencies programmatically.
#
# Notes about Makefiles:
#
# Makefile variables assigned with ':=' are expanded immediately and
# without recursion.
# 
# Variables that are assigned with '=' are expanded when they are used,
# which allows defining a variable in terms of other variables which are
# not yet assigned. However, expansions are recursive, which disallows
# self references to a variable when it is assigned, else inifinte
# recursion ensues.
# https://www.gnu.org/software/make/manual/make.html#Flavors
#
# The '?=' operator only assigns a variable if not already set,
# which allows overriding this variable externally
#
# When a prerequisite (dependency) is to the right of a | (pipe) symbol,
# it is an "order-only prerequisite" means Make only ensures that it is
# created before the target. Its "order" of creation matters. But its
# timestamp does not matter and is subsequently ignored for triggering
# rebuilds.

# Turn off trying to build targets with built-in rules; explicit rules
# must be present.
.SUFFIXES:

# This is the directory that all output goes to during the build process
BUILDDIR ?= $(CURDIR)/BUILD

default: $(BUILDDIR)/grammlex.jar
clean:
	rm -rf $(BUILDDIR) $(DEBUG_CLASSES) $(JUNIT_CLASSES)

# This is the list of source directories, space separated
SOURCE_DIRS := src/org/grammlex/v1

# Find every java source file in the list of source dirs
SOURCES := $(foreach dir,$(SOURCE_DIRS),$(wildcard $(dir)/*.java))

# This explains how the .java extenstion is mapped to .class:
# https://www.gnu.org/software/make/manual/make.html#Substitution-Refs
DEBUG_CLASSES := $(SOURCES:.java=.class) 
JUNIT_CLASSES := $(subst src/,src/test/,$(DEBUG_CLASSES:.class=Test.class)) src/test/org/grammlex/v1/AllTest.class
JUNIT_JAR ?= $(HOME)/.m2/repository/junit/junit/4.12/junit-4.12.jar
HAMCREST_JAR ?= $(HOME)/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

all: default debug test
# These the debug classes are classes built in the location that IntelliJ expects for debugging (the source directories).
# Build the "debug" target before launching the debugger
debug: $(DEBUG_CLASSES)
test: debug $(JUNIT_CLASSES)
	(cd src/test;java -cp .:..:$(JUNIT_JAR):$(HAMCREST_JAR) org.junit.runner.JUnitCore org.grammlex.v1.AllTest)

CLASSES := $(subst src/,$(BUILDDIR)/,$(SOURCES:.java=.class))
JAVAC := javac
JAR := jar

# Note that $(@F) is the (left side) target, file name only.
$(BUILDDIR)/grammlex.jar: $(CLASSES) $(BUILDDIR)/manifest.mf
	cd $(BUILDDIR) && $(JAR) cvfm $(@F) manifest.mf $(subst $(BUILDDIR)/,,$(CLASSES))

# The pipe symbol is explained in the top comment.
$(BUILDDIR)/manifest.mf: | $(BUILDDIR)
	cp src/manifest.mf $(BUILDDIR)/

$(BUILDDIR):
	mkdir -p $(BUILDDIR)

# This function is used to define a pattern rule that relates every java
# class file to a corresponding source file.
# The first parameter $(1) is the source directory.
# Note that functions $() and variables $< have an extra $ because
# it needs to be escaped in a function.
# The first rule is for the build directory.
# The second rule is for the DEBUG_CLASSES in the src directory.
# The third rule is for the JUNIT_CLASSES in the src/test directory.
define define_compile_rules
$(subst src/,$(BUILDDIR)/,$(1))/%.class: $(1)/%.java $(BUILDDIR)
	(cd src;$(JAVAC) -d $(BUILDDIR) $$(subst src/,,$$<))

$(1)/%.class: $(1)/%.java
	(cd src;$(JAVAC) $$(subst src/,,$$<))

$(subst src/,src/test/,$(1))/%.class: $(subst src/,src/test/,$(1))/%.java
	(cd src; test -f $(JUNIT_JAR) || { echo "Please set JUNIT_JAR to the correct location."; exit 1;})
	(cd src;$(JAVAC) -cp .:test:$(JUNIT_JAR) $$(subst src/,,$$<))
endef

# Here we call the function above for every source directory
$(foreach src_dir, $(SOURCE_DIRS), $(eval $(call define_compile_rules,$(src_dir))))

src/test/org/grammlex/v1/AllTest.class: src/test/org/grammlex/v1/AllTest.java
	(cd src;$(JAVAC) -cp .:test:$(JUNIT_JAR) $(subst src/,,$<))

# phony means that make will just run this target's commands, regardless of
# whether a file happens to exist with the same name
.PHONY: clean debug junit
