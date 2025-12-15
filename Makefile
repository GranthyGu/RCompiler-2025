JAVAC = javac
JAVA = java
OUTPUT_DIR = bin
SOURCES = $(shell find . -name "*.java")

.PHONY: build run clean

build:
	@mkdir -p $(OUTPUT_DIR)
	@$(JAVAC) -d $(OUTPUT_DIR) $(SOURCES)

run:
	@if [ -f builtin.c ]; then cat builtin.c >&2; fi
	@$(JAVA) -cp $(OUTPUT_DIR) rcompiler2025.src.Main

clean:
	@rm -rf $(OUTPUT_DIR)
