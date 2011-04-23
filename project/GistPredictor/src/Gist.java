import java.util.ArrayList;
import java.util.List;

public class Gist {
	private List<Double> gistValues = new ArrayList<Double>();
	private String label;

	public static Gist parseGistFromString(String gistString) {
		Gist gist = new Gist();
		String[] parts = gistString.split("\t");
		gist.label = parts[1];
		String[] gistStrVals = parts[0].split(",");
		for (String gistStrVal : gistStrVals) {
			gist.gistValues.add(Double.parseDouble(gistStrVal));
		}
		return gist;
	}
	
	public List<Double> getGistValues() {
		return gistValues;
	}

	public void setGistValues(List<Double> gistValues) {
		this.gistValues = gistValues;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public static int getGistLength() {
		return 2;
	}
}