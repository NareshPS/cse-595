GistTagPredictor.test: GistTagPredictor
	time java -Xmx2048M -cp ../lib/weka.jar:../lib/stemming.jar:../lib/jwnl.jar:../lib/commons-lang-2.6.jar:../lib/commons-logging-1.1.1.jar:. GistTagPredictor /scratch2/naresh/data/cat/cat-train /scratch2/naresh/data/cat/cat-test all_imgs.txt output.html smo>Out 2>Error &
GistTagPredictor: GistTagPredictor.java Gist.java GistFeatureManager.java
	javac -cp ../lib/weka.jar:../lib/stemming.jar:../lib/jwnl.jar:../lib/commons-lang-2.6.jar:../lib/commons-logging-1.1.1.jar *.java
clean: GistTagPredictor.class
	rm -rf *.class
	mv Out Out.bkp
	mv output.html output.html.bkp
	rm -rf output.html
	rm -rf Out
