
public class GistFileIdDumper {
	public static void main(String[] args) {
		StreamingFileUtil fUtil = new StreamingFileUtil(args[0]);
		String line = null;
		while ((line = fUtil.getNextLine()) != null) {
			String[] parts = line.split("\t");
			String filename = StringUtil.clean(parts[0]);
			filename = filename.replace(".jpg", "");
			String[] ids = filename.split("_");
			String valueStr = StringUtil.clean(parts[1]);
			valueStr = valueStr.replaceFirst(",$", "");
			System.out.println(ids[2] + "_" + ids[3] + "_" + ids[5] + "\t" + valueStr);
		}
		fUtil.close();
	}
}
