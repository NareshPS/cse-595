GistTagPredictor.test: GistTagPredictor
	time java -Xmx2048M -cp ../lib/weka.jar:. GistTagPredictor /scratch2/naresh/data/cat/cat-train /scratch2/naresh/data/cat/cat-test bayes>Out &
GistTagPredictor: GistTagPredictor.java Gist.java GistFeatureManager.java
	javac -cp ../lib/weka.jar *.java
clean: GistTagPredictor.class
	rm -rf *.class
	mv Out Out.bkp
	rm -rf Out
