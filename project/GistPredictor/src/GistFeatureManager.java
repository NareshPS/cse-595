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

	private int ParseGistFromFile(String fileName, Boolean fillFeatureTags) {
		File file               = new File(fileName);
		FileInputStream fis     = null;
		BufferedInputStream bis = null;
		DataInputStream dis     = null;
        int numDocs             = 0;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			String line = null;
			while ((line = dis.readLine()) != null) {
                numDocs++;
				Gist gist = Gist.parseGistFromString(line);
			//	docs.add(gist);
                if (fillFeatureTags) {
                    uniqueTags.add(gist.getLabel());

                    //Add tags to uniqueTags.
                    //These tags are the ones part of feature vector.
                    for (String tag: gist.getTagValues()) {
                        featureTags.add(tag);
                    }
                }
                gist    = null;
			}

			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numDocs;
	}

	private Instances CreateWekaFeatureSetInstances(String fileName, int numDocs, Boolean isTest) {
        File file               = new File(fileName);
		FileInputStream fis     = null;
		BufferedInputStream bis = null;
		DataInputStream dis     = null;

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
        System.out.println(numFeatures);

		// Weka featureSet
		Instances featureSet = new Instances("Features", wekaAttributes, numDocs);
		// Set class index
		featureSet.setClassIndex(0);
        
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			String line = null;
			while ((line = dis.readLine()) != null) {
				Gist gist = Gist.parseGistFromString(line);
                Instance anInstance = new Instance(numFeatures + 1);
                anInstance.setValue((Attribute) wekaAttributes.elementAt(0),
                        (isTest?uniqueTags.iterator().next():gist.getLabel()));
                int idx = 1;
                List<Double> gistValues = gist.getGistValues();
                List<String> tagValues  = gist.getTagValues();
                for (Double gistValue : gistValues) {
                    anInstance.setValue(
                            (Attribute) wekaAttributes.elementAt(idx++), gistValue);
                }
                //Add featureTags to weka.
                for (String tag: featureTags) {
                    if (tagValues.indexOf(tag) != -1) {
                        anInstance.setValue(
                                (Attribute) wekaAttributes.elementAt(idx++), new Double(0.9));
                    }
                    else {
                        anInstance.setMissing(idx++);
                                //(Attribute) wekaAttributes.elementAt(idx++),new Double(0.1));
                    }
                }
                featureSet.add(anInstance);
            }

			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return featureSet;
	}

	public GistFeatureManager(String trainFile, String testFile) {
		int numTrainDocs    = ParseGistFromFile(trainFile, true);
		int numTestDocs     = ParseGistFromFile(testFile, false);
		trainInstances      = CreateWekaFeatureSetInstances(trainFile, numTrainDocs, false);
		testInstances       = CreateWekaFeatureSetInstances(testFile, numTestDocs, true);
	}

	public Instances GetTrainInstances() {
		return trainInstances;
	}
    
    public Instances GetTestInstances() {
		return testInstances;
	}
}
