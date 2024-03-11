# Makefile for Kotlin project

KOTLINC=kotlinc
KOTLINFLAGS=-include-runtime
JARNAME=VMTranslator.jar
MAINCLASS=MainKt
ENTRYSCRIPT=VMTranslator

all: $(ENTRYSCRIPT)

$(ENTRYSCRIPT): $(JARNAME)
	echo '#!/bin/sh' > $(ENTRYSCRIPT)
	echo 'java -jar $(PWD)/$(JARNAME) $$1' >> $(ENTRYSCRIPT)
	chmod +x $(ENTRYSCRIPT)

$(JARNAME):
	$(KOTLINC) main.kt $(KOTLINFLAGS) -d $(JARNAME)

clean:
	rm -f $(JARNAME) $(ENTRYSCRIPT)
