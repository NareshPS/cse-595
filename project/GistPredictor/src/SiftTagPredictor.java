import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import weka.classifiers.Classifier;
import weka.core.SparseInstance;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class SiftTagPredictor {
	private static final int	TagPredictThreshold	= 20;

	public static void main(String[] args) throws Exception {

		System.err.println("Building tag model ... ");
		TagModel tagModel = new TagModel(args[0]);
		System.err.println("done.");

		System.err.print("Deserializing classifier model ... ");
		Classifier cls = (Classifier) weka.core.SerializationHelper
		    .read(new FileInputStream(new File(args[1])));
		System.err.println("done.");

		List<String> allTags = new ArrayList<String>();
		Scanner tagsIn = new Scanner(new FileReader(args[2]));
		while (tagsIn.hasNextLine())
			allTags.add(tagsIn.nextLine());

		FastVector wekaAttributes = new FastVector(Gist.getGistLength() + 1);

		FastVector classValues = new FastVector(allTags.size());
		for (String tag : allTags) {
			classValues.addElement(tag);
		}

		Attribute classAttribute = new Attribute("tagClass", classValues);
		wekaAttributes.addElement(classAttribute);

		// Add feature names.
		for (int i = 1; i <= Gist.getGistLength(); ++i) {
			wekaAttributes.addElement(new Attribute("gist_" + i));
		}

		List<SparseInstance> instances = new ArrayList<SparseInstance>();

		// Rad in the gist feature file and predict for all the instances
		// ony to prepare the set of instances
		Scanner in = new Scanner(new FileReader(args[3]));
		while (in.hasNextLine()) {
			Gist gist = Gist.parseGistFromString(in.nextLine());

			// Add the class attribute.
			SparseInstance testInstance = new SparseInstance(Gist.getGistLength() + 1);

			int idx = 1;
			List<Double> gistValues = gist.getGistValues();
			for (Double gistValue : gistValues) {
				testInstance.setValue((Attribute) wekaAttributes.elementAt(idx++),
				    gistValue);
			}

			testInstance.setValue((Attribute) wekaAttributes.elementAt(0),
			    allTags.get(0));

			instances.add(testInstance);
		}

		Instances featureSet = new Instances("Features", wekaAttributes,
		    instances.size());
		in.close();

		for (Instance instance : instances)
			featureSet.add(instance);

		in = new Scanner(new FileReader(args[3]));

		for (int fi = 0; fi < featureSet.numInstances(); ++fi) {
			Instance testInstance = featureSet.instance(fi);

			Gist gist = Gist.parseGistFromString(in.nextLine());

			List<String> originalTagList = new ArrayList<String>();

			for (String originalTag : gist.getLabel().split("\\|")) {
				originalTagList.add(StringUtil.clean(originalTag));
			}

			Collections.shuffle(originalTagList);

			Set<String> excludedTags = new HashSet<String>();
			Set<String> trainTags = new HashSet<String>();
			
			int EXCLUDED = 5;
			
			for(int i = 0; i < EXCLUDED; ++i) {
				excludedTags.add(originalTagList.get(i));
			}

			for(int i = EXCLUDED; i < originalTagList.size(); ++i) {
				trainTags.add(originalTagList.get(i));
			}

			// get twice the number of tags we want to predict
			Set<WordScore> tagScores = tagModel.getBestKTags(StringUtils.join(trainTags, "|"),
			    TagPredictThreshold * 2);

			double[] probabilities = cls.distributionForInstance(testInstance);

			List<WordScore> testScores = new ArrayList<WordScore>();
			for (int i = 0; i < allTags.size(); ++i) {
				testScores.add(new WordScore(allTags.get(i), probabilities[i]));
			}

			Collections.sort(testScores);

			List<WordScore> intersection = new ArrayList<WordScore>();
			int numPredicted = 0;
			for (WordScore classifiedScore : testScores) {
				for (WordScore modelScore : tagScores) {
					if (classifiedScore.word.equals(modelScore.word)) {
						++numPredicted;
						intersection.add(new WordScore(modelScore.word, modelScore.score
						    * classifiedScore.score));
					}
				}

				if (numPredicted >= TagPredictThreshold)
					break;
			}

			Collections.sort(intersection);

			System.out.println(gist.getFileId());
			
			System.out.println(StringUtils.join(excludedTags.toArray(), ","));

			WordScore[][] allScores = new WordScore[][] {
			    tagScores.toArray(new WordScore[] {}),
			    testScores.toArray(new WordScore[] {}),
			    intersection.toArray(new WordScore[] {}) };
			for (WordScore[] scores : allScores) {
				System.out.println(StringUtils.join(
				    ArrayUtils.subarray(scores, 0, TagPredictThreshold), ","));
			}

			double total = 0;
			for (WordScore[] scores : allScores) {
				int count = 0;
				for (int i = 0; i < 5 && i < scores.length; ++i) {
					if (trainTags.contains(scores[i].word)) {
						count += 1;
					}
				}

				total += count;
				System.out.print(count + " ");
			}

			System.out.println(total / allScores.length);

			total = 0;
			for (WordScore[] scores : allScores) {
				int count = 0;
				for (int i = 0; i < scores.length; ++i) {
					if (excludedTags.contains(scores[i].word)) {
						count += 1;
					}
				}

				total += count;
				System.out.print(count + " ");
			}
			System.out.println(total / allScores.length);

			System.out.println();
			System.out.println();

		}
	}
}
