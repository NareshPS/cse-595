import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import java.lang.Integer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class GistFeatureManager {
	private Instances trainInstances;
	private Instances testInstances;
	private Set<String> uniqueTags          = new TreeSet<String>();
	private static Set<String> featureTags  = new TreeSet<String>();

	private List<Gist> ParseGistFromFile(String fileName, Boolean fillFeatureTags) {
		File file = new File(fileName);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		List<Gist> docs = new ArrayList<Gist>();
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			String line = null;
			while ((line = dis.readLine()) != null) {
				Gist gist = Gist.parseGistFromString(line);
				docs.add(gist);
                if (fillFeatureTags) {
                    uniqueTags.add(gist.getLabel());

                    //Add tags to uniqueTags.
                    //These tags are the ones part of feature vector.
                    for (String tag: gist.getTagValues()) {
                        featureTags.add(tag);
                    }
                }
			}

			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return docs;
	}

	private Instances CreateWekaFeatureSetInstances(List<Gist> docs) {
		int numFeatures = Gist.getGistLength() + featureTags.size() + 1;

		FastVector wekaAttributes = new FastVector(numFeatures + 1);

		// Add the class attribute.
		FastVector classValues = new FastVector(uniqueTags.size());
		for (String tag : uniqueTags) {
			classValues.addElement(tag);
		}
		Attribute classAttribute = new Attribute("tagClass", classValues);
		wekaAttributes.addElement(classAttribute);

		// Add feature names.
		for (int i = 1; i <= Gist.getGistLength(); ++i) {
			wekaAttributes.addElement(new Attribute("gist_" + i));
		}

        // Add tags as features.
        for (String tag: featureTags) {
            wekaAttributes.addElement(new Attribute("tag_" + tag));
        }

		// Weka featureSet
		Instances featureSet = new Instances("Features", wekaAttributes,
				docs.size());
		// Set class index
		featureSet.setClassIndex(0);

		for (Gist doc : docs) {
			Instance anInstance = new Instance(numFeatures + 1);
			anInstance.setValue((Attribute) wekaAttributes.elementAt(0),
					doc.getLabel());
			int idx = 1;
			List<Double> gistValues = doc.getGistValues();
            List<String> tagValues  = doc.getTagValues();
			for (Double gistValue : gistValues) {
				anInstance.setValue(
						(Attribute) wekaAttributes.elementAt(idx++), gistValue);
			}
            //Add featureTags to weka.
            for (String tag: featureTags) {
                if (tagValues.indexOf(tag) != -1) {
                    System.out.print(tag + "=1,");
				    anInstance.setValue(
						    (Attribute) wekaAttributes.elementAt(idx++), new Double(0.9));
                }
                else {
                    System.out.print(tag + "=0,");
				    anInstance.setMissing(idx++);
						    //(Attribute) wekaAttributes.elementAt(idx++),new Double(0.1));
                }
            }
            System.out.println("");
			featureSet.add(anInstance);
		}
		return featureSet;
	}

	public GistFeatureManager(String trainFile, String testFile) {
		List<Gist> trainingDocs = ParseGistFromFile(trainFile, true);
		List<Gist> testingDocs  = ParseGistFromFile(testFile, false);
		trainInstances  = CreateWekaFeatureSetInstances(trainingDocs);
		testInstances   = CreateWekaFeatureSetInstances(testingDocs);
	}

	public Instances GetTrainInstances() {
		return trainInstances;
	}
    
    public Instances GetTestInstances() {
		return testInstances;
	}
}
