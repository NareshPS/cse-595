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
import weka.core.SparseInstance;

public class GistFeatureManager {
	private Instances trainInstances;
	private Instances testInstances;
	private Set<String> uniqueTags          = new TreeSet<String>();

    private InstanceImageMap    theMap; 
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
                if (fillFeatureTags) {
                    uniqueTags.add(gist.getLabel());
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

		int numFeatures = Gist.getGistLength() + 1;

		FastVector wekaAttributes = new FastVector(numFeatures);

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
                SparseInstance anInstance = new SparseInstance(numFeatures);
                anInstance.setValue((Attribute) wekaAttributes.elementAt(0),
                        (isTest?uniqueTags.iterator().next():gist.getLabel()));
                int idx = 1;
                List<Double> gistValues = gist.getGistValues();
                theMap.addImageId(gist.getFileId());
                for (Double gistValue : gistValues) {
                    anInstance.setValue(
                            (Attribute) wekaAttributes.elementAt(idx++), gistValue);
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

	public GistFeatureManager(String trainFile, String testFile, String imageIdFile) {
        theMap              = InstanceImageMap.constructMap(imageIdFile);
        if (theMap == null) {
            System.out.println("Failed to parse imageIdFile");
        }
		int numTrainDocs    = ParseGistFromFile(trainFile, true);
		int numTestDocs     = ParseGistFromFile(testFile, false);
		trainInstances      = CreateWekaFeatureSetInstances(trainFile, numTrainDocs, false);
		testInstances       = CreateWekaFeatureSetInstances(testFile, numTestDocs, true);
	}

    public InstanceImageMap getImageMap() {
        return theMap;
    }

	public Instances GetTrainInstances() {
		return trainInstances;
	}
    
    public Instances GetTestInstances() {
		return testInstances;
	}
}
