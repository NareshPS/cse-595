import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import weka.classifiers.Classifier;
import weka.core.SparseInstance;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class SiftTagPredictorOneToAll {
	private static final int	TagPredictThreshold	= 20;

	public static void main(String[] args) throws Exception {

		System.err.println("Building tag model ... ");
		TagModel tagModel = new TagModel(args[0]);
		System.err.println("done.");

		Map<String, List<String>> tagToClasses = new HashMap<String, List<String>>();

		System.err.println("Reading tag classes ... ");
		Scanner classifiersIn = new Scanner(new FileReader(args[1]));
		while (classifiersIn.hasNextLine()) {
			String line = classifiersIn.nextLine();
			String tag = line.substring(line.lastIndexOf('/') + 1, line.indexOf('.'));
			System.err.println("Reading tag " + tag);

			tagToClasses.put(tag, new ArrayList<String>());
			Scanner classIn = new Scanner(new FileReader(line.replace(".classifier.",
			    ".labels.")));
			while (classIn.hasNextLine()) {
				tagToClasses.get(tag).add(classIn.nextLine());
			}

			System.err.println("found "
			    + StringUtils.join(tagToClasses.get(tag).toArray(), ","));

			classIn.close();
		}
		classifiersIn.close();
		System.err.println("done.");

		FastVector wekaAttributes = new FastVector(Gist.getGistLength() + 1);

		String[] classLabels = new String[] { "tag", "not_tag" };
		FastVector classValues = new FastVector(classLabels.length);
		for (String tag : classLabels) {
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
			    classLabels[0]);

			instances.add(testInstance);
		}

		in.close();
		Instances featureSet = new Instances("Features", wekaAttributes,
		    instances.size());

		for (Instance instance : instances)
			featureSet.add(instance);

		in = new Scanner(new FileReader(args[3]));

		classifiersIn = new Scanner(new FileReader(args[1]));
		Map<String, Map<String, WordScore>> tagIdScoreMap = new HashMap<String, Map<String, WordScore>>();
		while (classifiersIn.hasNextLine()) {
			String line = classifiersIn.nextLine();
			String tag = line.substring(line.lastIndexOf('/') + 1, line.indexOf('.'));

			System.err.println("Loading classifier for tag: " + tag);

			Map<String, WordScore> idScoreMap = new HashMap<String, WordScore>();

			Classifier cls = (Classifier) weka.core.SerializationHelper.read(line);

			for (int fi = 0; fi < featureSet.numInstances(); ++fi) {
				Instance testInstance = featureSet.instance(fi);
				Gist gist = Gist.parseGistFromString(in.nextLine());

				double[] probabilities = cls.distributionForInstance(testInstance);

				idScoreMap.put(gist.getFileId(), new WordScore(tag,
				    probabilities[tagToClasses.get(tag).indexOf(tag)]));
			}

			tagIdScoreMap.put(tag, idScoreMap);

			cls = null;
			System.gc();
		}
		classifiersIn.close();
		in.close();

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

			int EXCLUDED = 5 < originalTagList.size() ? 5
			    : originalTagList.size() - 1;

			for (int i = 0; i < EXCLUDED; ++i) {
				excludedTags.add(originalTagList.get(i));
			}

			for (int i = EXCLUDED; i < originalTagList.size(); ++i) {
				trainTags.add(originalTagList.get(i));
			}

			// get twice the number of tags we want to predict
			Set<WordScore> tagScores = tagModel.getBestKTags(
			    StringUtils.join(trainTags, "|"), TagPredictThreshold * 2);

			// TODO: get probabilities for each word
			List<WordScore> testScores = new ArrayList<WordScore>();
			for(String tag : tagIdScoreMap.keySet()){
				testScores.add(tagIdScoreMap.get(tag).get(gist.getFileId()));
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
