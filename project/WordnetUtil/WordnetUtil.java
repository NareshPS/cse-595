import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.TreeSet;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class WordnetUtil {

	private static WordnetUtil wordnetUtil = null;
	private Dictionary dictionary;
	private final Integer SYN_THRESHOLD = 1;

	private WordnetUtil() throws FileNotFoundException, JWNLException {
		JWNL.initialize(new FileInputStream(
				"/disk01/rohith/wap/wordnet/file_properties.xml"));
		dictionary = Dictionary.getInstance();
	}

	public static WordnetUtil getInstance() throws FileNotFoundException,
			JWNLException {
		if (wordnetUtil == null) {
			wordnetUtil = new WordnetUtil();
		}
		return wordnetUtil;
	}

	@SuppressWarnings("unchecked")
	public Set<String> expandWord(String word) throws JWNLException {
		Set<String> expansions = new TreeSet<String>();
		IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, word);
		if (indexWord != null) {
			Synset[] synsets = indexWord.getSenses();
			int i = 0;
			for (Synset synset : synsets) {
				Word[] synWords = synset.getWords();
				for (Word synWord : synWords) {
					expansions.add(synWord.getLemma().trim().toLowerCase());
				}
				if (++i >= SYN_THRESHOLD) {
					break;
				}
			}
		}
		return expansions;
	}

	public static void main(String[] args) throws JWNLException, FileNotFoundException {
		WordnetUtil wnUtil = WordnetUtil.getInstance();
		Set<String> words = wnUtil.expandWord("dog");
		for (String word : words) {
			System.out.println(word);
		}
	}
}