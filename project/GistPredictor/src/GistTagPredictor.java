import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.J48graft;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.SparseInstance;

public class GistTagPredictor {

	@SuppressWarnings("rawtypes")
	public static class LabelScore implements Comparable {
		private String label;
		private double score;

		public LabelScore(String label, double score) {
			this.label = label;
			this.score = score;
		}
		
		public String getLabel() {
			return label;
		}

		public double getScore() {
			return score;
		}
		
		@Override
		public int compareTo(Object other) {
			LabelScore otherLabelScore = (LabelScore)other;
			if (this.score > otherLabelScore.score) {
				return -1;
			} else if (this.score < otherLabelScore.score) {
				return 1;
			} else {
				return this.label.compareTo(otherLabelScore.label);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] argv) throws Exception {
		if (argv.length < 5) {
			System.err
					.println("Usage: <program> <train-data> <test-data> <image-id-map-file> <output.html> <classifier-class>");
			return;
		}

		// Create instances of required classifiers.
		HashMap<String, Classifier> classifierMap = new HashMap<String, Classifier>();
        SMO smo = new SMO();
        smo.setKernel(new RBFKernel()); 
		classifierMap.put("smo", smo);
		classifierMap.put("bayes", new NaiveBayes());
		classifierMap.put("bayesnet", new BayesNet());
		classifierMap.put("bayesmulti", new NaiveBayesMultinomial());

		classifierMap.put("slr", new SimpleLinearRegression());
		classifierMap.put("logitboost", new LogitBoost());
		classifierMap.put("adaboost", new AdaBoostM1());
		classifierMap.put("decisionstump", new DecisionStump());
		classifierMap.put("dtrees", new J48());
		classifierMap.put("dtreesgraft", new J48graft());
		classifierMap.put("knn", new IBk());
		classifierMap.put("rulebased", new PART());

		// Check classifier string.
		if (!classifierMap.containsKey(argv[4])) {
			System.err
					.println("Classifier not present: Use one of the following:");
			System.err.println("smo,bayes,logitboost,adaboost,decisionstump"
					+ "dtrees,dtreesgraft,knn,rulebased");
		}

		Classifier classifier = null;
		
		// Create training and test data.
		GistFeatureManager featMgr = new GistFeatureManager(argv[0], argv[1], argv[2]);
		Instances trainingSet = featMgr.GetTrainInstances();
		//Instances testingSet = featMgr.GetTestInstances();

		// Choose the classifier.
		classifier = classifierMap.get(argv[4]);
		classifier.buildClassifier(trainingSet);
		if (argv.length == 5) {
			File classifierOut = new File("/scratch2/rohith/wap/classifier.out");
			FileOutputStream fos = new FileOutputStream(classifierOut);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			SerializationHelper.write(bos, classifier);
		}
        //Initialize HTMLWriter.
        //HTMLHandler     htmlWriter   = new HTMLHandler(argv [3]); 
		
		/*
		Attribute classAttribute = trainingSet.attribute("tagClass");
		int numValues = classAttribute.numValues();
		List<String> labels = new ArrayList<String>();
		for (int i = 0; i < numValues; ++i) {
			labels.add(classAttribute.value(i));
		}
		
		for (int i = 0; i < testingSet.numInstances(); ++i) {
			SparseInstance testInstance = (SparseInstance)testingSet.instance(i);
			double[] probabilities = classifier.distributionForInstance(testInstance);
			SortedSet<LabelScore> labelScores = new TreeSet<LabelScore>();
			for (int j = 0; j < numValues; ++j) {
				labelScores.add(new LabelScore(labels.get(j), probabilities[j]));
			}
            String  captions    = new String();
			int j = 0;
			System.out.println("============");
			for (LabelScore labelScore : labelScores) {
                captions += labelScore.getLabel() + "," + labelScore.getScore() + "\t";
				System.out.println(labelScore.getLabel() + "," + labelScore.getScore());
				if (j++ > 20) {
					break;
				}
			}
			System.out.println("============");
            htmlWriter.addImage(featMgr.getImageMap().getImage(i), captions);
		}

        htmlWriter.close();
		*/
	}
}
