import java.util.ArrayList;
import java.util.List;

public class Gist {
	private List<Double> gistValues = new ArrayList<Double>();
	private List<String> tagValues = new ArrayList<String>();
	private String label;
    private int tagCount;

	public static Gist parseGistFromString(String gistString) {
		Gist gist = new Gist();
		String[] parts = gistString.split("\t");
		gist.label = parts[1];
		String[] gistStrVals = parts[0].split(",");
        int tagOffset;

        // Copy the GIST values in the gistValues vector.
        // The size of gistValues vector equals getGistLength()

        for (int i = 0; i < getGistLength(); i++) {
			gist.gistValues.add(new Double(gistStrVals [i]));
        }

        // Populate tagOffset. This should point to the index
        // in gistStrVals where tags start.
        tagOffset   = gist.getGistLength();
        // Store the tagCount
        gist.setTagCount(gistStrVals.length-tagOffset);
        
        for (int i = 0; i < gist.getTagCount(); i++) {
            gist.tagValues.add(gistStrVals [tagOffset+i]);
        }
        
        return gist;
	}

    public void setTagCount(int tagCount) {
        this.tagCount   = tagCount;
    }

    public int getTagCount() {
        return this.tagCount;
    }

    public List<String> getTagValues() {
        return tagValues;
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
		return 960;
	}
}
