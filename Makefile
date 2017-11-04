all : Project.class

Project.class : 
	javac Project.java

clean : 
	rm -rf ./*.class