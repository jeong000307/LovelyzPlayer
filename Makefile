all : Project.class

Project.class : clean
	javac -cp ./jaudiotagger-2.2.3.jar main.java
	java -cp ./jaudiotagger-2.2.3.jar main

clean : 
	rm -rf ./*.class