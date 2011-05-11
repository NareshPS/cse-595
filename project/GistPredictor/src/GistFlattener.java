
public class GistFlattener {
	public static void main(String[] args) {
		StreamingFileUtil fUtil = new StreamingFileUtil(args[0]);
		String line = null;
		while ((line = fUtil.getNextLine()) != null) {
			String[] parts = line.split("\t");
			if (parts.length < 3) {
				continue;
			}
			String[] tags = parts[2].split("\\|");
			for (String tag : tags) {
				String cleanedTag = StringUtil.clean(tag);
				if (cleanedTag.matches(".*[^a-zA-Z ,].*")) {
					continue;
				}
				System.out.println(StringUtil.clean(parts[0]) + "\t" +
						StringUtil.clean(parts[1]) + "\t" +
						cleanedTag);
			}
		}
		fUtil.close();
	}
}