all : play

play : install
	java -jar main.jar

install : configure
	jar -cvmf manifest.txt main.jar *.class

configure : clean
	javac main.java

clean : 
	rm -rf ./*.class ./*.jar