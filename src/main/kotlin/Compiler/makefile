# Makefile for Kotlin project

KOTLINC=kotlinc
KOTLINFLAGS=-include-runtime
JARNAME=JackCompiler.jar
MAINCLASS=MainKt
ENTRYSCRIPT=JackCompiler
all: $(ENTRYSCRIPT)

$(ENTRYSCRIPT): $(JARNAME)
	echo '#!/bin/sh' > $(ENTRYSCRIPT)
	echo 'java -jar $(PWD)/$(JARNAME) $$1' >> $(ENTRYSCRIPT)
	chmod +x $(ENTRYSCRIPT)

$(JARNAME):
	$(KOTLINC) JackCompiler.kt $(KOTLINFLAGS) -d $(JARNAME)

clean:
	rm -f $(JARNAME) $(ENTRYSCRIPT)
