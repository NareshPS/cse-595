import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

class WordScore implements Comparable<WordScore> {
	public String word;
	public Double score;

	WordScore(String word, Double score) {
		this.word = word;
		this.score = score;
	}

	@Override
	public int compareTo(WordScore otherWs) {
		if (score < otherWs.score) {
			return 1;
		} else if (score > otherWs.score) {
			return -1;
		} else {
			return this.word.compareTo(otherWs.word);
		}
	}
}

public class TagModel {
	private HashMap<String, HashMap<String, Integer>> cooccurMap = new HashMap<String, HashMap<String,Integer>>();
	private HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
	
	TagModel(String filename) {
		StreamingFileUtil fUtil = new StreamingFileUtil(filename);
		String line = null;
		int count = 1;
		while ((line = fUtil.getNextLine()) != null) {
			//System.out.println(count++);
			String[] tags = line.split("\\|");
			for (String tag1 : tags) {
				String cleanedTag1 = StringUtil.clean(tag1);
				if (cleanedTag1.matches(".*[^a-zA-Z ,].*")) {
					continue;
				}
				HashMap<String, Integer> level2Map = cooccurMap.get(cleanedTag1);
				if (level2Map == null) {
					level2Map = new HashMap<String, Integer>();
					cooccurMap.put(cleanedTag1, level2Map);
				}
				
				Integer freq = freqMap.get(cleanedTag1);
				if (freq == null) {
					freqMap.put(cleanedTag1, 1);
				} else {
					freqMap.put(cleanedTag1, freq + 1);
				}
				
				for (String tag2 : tags) {
					String cleanedTag2 = StringUtil.clean(tag2);

					if (cleanedTag1.matches(".*[^a-zA-Z ,].*")) {
						continue;
					}
					
					if (cleanedTag2.matches(".*[^a-zA-Z ,].*")) {
						continue;
					}
					
					Integer cooccur = level2Map.get(cleanedTag2);
					if (cooccur == null) {
						level2Map.put(cleanedTag2, 1);
					} else {
						level2Map.put(cleanedTag2, cooccur + 1);
					}
				}
			}
		}
		fUtil.close();
		/*
		for (String word1 : cooccurMap.keySet()) {
			ArrayList<Integer> cooccurList = new ArrayList<Integer>();
			for (String word2 : cooccurMap.get(word1).keySet()) {
				cooccurList.add(cooccurMap.get(word1).get(word2));
			}
			Collections.sort(cooccurList);
			int median = cooccurList.get(cooccurList.size()/2);
			for (String word2 : cooccurMap.get(word1).keySet()) {
				HashMap<String, Integer> level2Map = cooccurMap.get(word1);
				Integer cooccur = level2Map.get(word2);
				level2Map.put(word2, cooccur * 100/(cooccur + median));
			}
		}
		*/
	}
	
	public void debug() {
		for (String tag1 : freqMap.keySet()) {
			System.out.println("Freq of " + tag1 + " = " + freqMap.get(tag1));
		}
		
		for (String tag1 : cooccurMap.keySet()) {
			for (String tag2 : cooccurMap.get(tag1).keySet()) {
				System.out.println(tag1 + "," + tag2 + " = " + cooccurMap.get(tag1).get(tag2));
			}
		}
	}
	
	public TreeSet<WordScore> getBestKTags(String line, int k) {
		TreeSet<WordScore> bestTags = new TreeSet<WordScore>();
		String[] tags = line.split("\\|");
		
		Integer numTags = freqMap.size();
		
		for (String tag1 : freqMap.keySet()) {
			double curTagProb = 0.0;
			for (String tag2 : tags) {
				String cleanedTag2 = StringUtil.clean(tag2);
				if (tag1.compareTo(cleanedTag2) == 0) {
					continue;
				}
				
				HashMap<String, Integer> level2Map = cooccurMap.get(tag1);
				Integer cooccur = null;
				if (level2Map != null) {
					cooccur = level2Map.get(cleanedTag2);
					if (cooccur == null) {
						cooccur = 1;
					} else {
						cooccur++;
					}
				} else {
					cooccur = 1;
				}
				

				Integer freq = null;
				freq = freqMap.get(cleanedTag2);
				if (freq != null) {
					freq += numTags;
				} else {
					freq = numTags;
				}
				
				double curCondProb = Math.log((double)cooccur/(double)freq);
				curTagProb += curCondProb;
			}
			bestTags.add(new WordScore(tag1, curTagProb));
			if (bestTags.size() > k) {
				bestTags.remove(bestTags.last());
			}
		}
		return bestTags;
	}
	
	public static void main(String[] args) {
		TagModel tagModel = new TagModel(args[0]);
		//tagModel.debug();
		StreamingFileUtil fUtil = new StreamingFileUtil(args[1]);
		String line = null;
		while ((line = fUtil.getNextLine()) != null) {
			TreeSet<WordScore> bestTags = tagModel.getBestKTags(line, 40);
			System.out.println("Input tags: " + line);
			for (WordScore tag : bestTags) {
				System.out.print(tag.word + ",");
			}
			System.out.println("\n");
		}
	}
}