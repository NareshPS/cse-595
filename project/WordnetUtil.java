import java.util.ArrayList;
import java.util.List;

public class WordnetUtil {

  private static final WordnetUtil wordnetUtil = new WordnetUtil();

  private WordnetUtil() {}

  public static WordnetUtil getInstance() {
    return wordnetUtil;
  }
  
  public List<String> expandWord(String word) {
    List<String> expansions = new ArrayList<String>();
    return expansions;
  }
}
  
  