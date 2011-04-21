
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.Map.Entry;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.fs.*;

public class GoogleGists {

  public Hashtable<String, List<Double[]>> googleGistsByCategory = new Hashtable<String, List<Double[]>>();

  public void initializeGospelGists(FileSystem fs, Path hdfsDirectory) throws IOException {
    for(FileStatus status : fs.listStatus(hdfsDirectory)) {
      Path dataFilePath = status.getPath();

      Scanner scanner = new Scanner(fs.open(dataFilePath));

      while(scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();

        // get the category
        int lastSlash = line.lastIndexOf("/");

        // Hard coding the path prefix
        int folderPathLength = "/user/naresh/GoogleImages/".length();
        String category = line.substring(folderPathLength, lastSlash);

        // get the associated data
        int dataIndex = line.lastIndexOf("\t") + 1;
        String data = line.substring(dataIndex);
        
        String[] numbers = data.split(",");

        ArrayList<Double> thisVector = new ArrayList<Double>();

        for(String number : numbers) {
          number = number.trim();
          if(number.length() > 0) {
            thisVector.add(Double.parseDouble(number));
          }
        }

        if(!googleGistsByCategory.keySet().contains(category)) {
          googleGistsByCategory.put(category, new ArrayList<Double[]>());
        }

        googleGistsByCategory.get(category).add(thisVector.toArray(new Double[] {}));
      }
    }
  }

  public GoogleGists(FileSystem fs, Path hdfsDirectory) throws IOException {
    initializeGospelGists(fs, hdfsDirectory);
  }
}
