all : play

play : install
	java -jar LovelyzPlayer.jar

install : configure
	jar -cvmf manifest.txt LovelyzPlayer.jar *.class

configure : clean
	javac LovelyzPlayer.java

clean : 
	rm -rf ./*.class ./*.jar