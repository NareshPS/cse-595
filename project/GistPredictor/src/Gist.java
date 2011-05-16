import java.util.ArrayList;
import java.util.List;

public class Gist {
	private List<Double> gistValues = new ArrayList<Double>();
	private List<String> tagValues = new ArrayList<String>();
	private String label;
    private String fileId;
    private int tagCount;

	public static Gist parseGistFromString(String gistString) {
		Gist gist = new Gist();
		String[] parts          = gistString.split("\t");
		String[] gistStrVals    = parts[1].split(",");
		gist.fileId             = StringUtil.clean(parts[0]);
        gist.label = StringUtil.clean(gist.stripColon(parts[2]));

        // Copy the GIST values in the gistValues vector.
        // The size of gistValues vector equals getGistLength()

        for (int i = 0; i < getGistLength(); i++) {
			gist.gistValues.add(new Double(gistStrVals [i]));
        }

        return gist;
	}

    public String stripColon(String str) {
    	String ret = null;
    	if (str != null) {
    		ret = StringUtil.clean(str.replace(":", ""));
    	}
    	return ret;
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
		return 1050;
	}

    public String getFileId() {
        return fileId;
    }
}
