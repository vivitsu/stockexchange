JFLAGS = -g -cp .:jgroups-3.2.7.Final.jar
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	VSynchrony.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -name \*.class | xargs $(RM) 
	$(RM) log*
