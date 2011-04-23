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

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class GistFeatureManager {
	private Instances instances;
	private Set<String> uniqueTags = new TreeSet<String>();

	private List<Gist> ParseGistFromFile(String fileName) {
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
				uniqueTags.add(gist.getLabel());
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
		int numFeatures = Gist.getGistLength() + 1;

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
			for (Double gistValue : gistValues) {
				anInstance.setValue(
						(Attribute) wekaAttributes.elementAt(idx++), gistValue);
			}
			featureSet.add(anInstance);
		}
		return featureSet;
	}

	public GistFeatureManager(String inputFile) {
		List<Gist> trainingDocs = ParseGistFromFile(inputFile);
		instances = CreateWekaFeatureSetInstances(trainingDocs);
	}

	public Instances GetInstances() {
		return instances;
	}
}