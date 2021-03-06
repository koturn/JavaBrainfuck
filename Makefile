JAVAC        := javac
JAR          := jar
JAVADOC      := javadoc
ECHO         := echo
MKDIR        := mkdir -p
RM           := rm -rf

SRC_DIR      := src
BIN_DIR      := bin
JAVADOC_DIR  := javadoc
MAIN         := Main
TARGET       := $(lastword $(subst ., ,$(MAIN))).jar
JD_INDEX     := $(JAVADOC_DIR)/index.html
MAIN_SRC     := $(SRC_DIR)/$(subst .,/,$(MAIN)).java
MAIN_BIN     := $(BIN_DIR)/$(subst .,/,$(MAIN)).class
MANIFEST     := MANIFEST.MF
SRCS         := $(MAIN_SRC)
OBJS         := $(MAIN_BIN)

SRC_CHARSET  := utf-8
DST_CHARSET  := utf-8
JAVAFLAGS    := -sourcepath $(SRC_DIR) -encoding $(SRC_CHARSET) -d $(BIN_DIR) \
                $(if $(SRC_VERSION),-source $(SRC_VERSION),) \
                $(if $(BIN_VERSION),-target $(BIN_VERSION),)
JARFLAGS     := cvfm
JAVADOCFLAGS := -sourcepath $(SRC_DIR) -encoding $(SRC_CHARSET) -d $(JAVADOC_DIR) \
                -charset $(DST_CHARSET) -docencoding $(DST_CHARSET) -private


.PHONY: all javadoc clean cleanobj
all: $(TARGET)

$(TARGET): $(OBJS) $(MANIFEST)
	$(JAR) $(JARFLAGS) $@ $(MANIFEST) -C $(BIN_DIR) .

$(MANIFEST):
	$(ECHO) "Main-Class: $(MAIN)" > $(MANIFEST)

$(OBJS): $(SRCS)
	@[ ! -d $(@D) ] && $(MKDIR) $(@D) || :
	$(JAVAC) $(JAVAFLAGS) $(MAIN_SRC)


javadoc: $(JD_INDEX)

$(JD_INDEX): $(wildcard $(SRC_DIR)/*.java $(SRC_DIR)/org/koturn/brainfuck/*.java $(SRC_DIR)/org/koturn/util/*.java)
	$(JAVADOC) $(JAVADOCFLAGS) $^


clean:
	$(RM) $(TARGET) $(BIN_DIR)/*.class $(JAVADOC_DIR)/*

objclean:
	$(RM) $(BIN_DIR)/*.class $(JAVADOC_DIR)/*
