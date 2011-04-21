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

public class DistanceCalculatorMR {
  
  public static class Map extends Mapper<LongWritable, Text, Text, Text> {
    GoogleGists                  gg   = null;
    Configuration                conf = null;
    public IDistanceCalculator[] dcs  = null;
    
    @Override
    public void setup(Context context) {
      try {
        conf = context.getConfiguration();
        dcs = new IDistanceCalculator[] { new EuclideanDC(), new CosineDC() };
        System.out.println(((FileSplit) context.getInputSplit()).getPath()
            .getName());
      } catch (IOException ioe) {
        System.out.println(ioe.toString());
      }
    }
    
    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
      if (null == gg) {
        gg = new GoogleGists(FileSystem.get(conf), new Path(
            conf.get("googleImages.dir")));
      }
      
      String line = value.toString();
      String data = line.substring(line.indexOf("\t") + 1);
      String image = line.substring(0, line.indexOf("\t"));
      String[] numbers = data.split(",");
      
      System.out.println("Image: " + image);
      
      ArrayList<Double> vector = new ArrayList<Double>();
      
      for (String number : numbers) {
        number = number.trim();
        
        if (number.length() > 0) {
          vector.add(Double.parseDouble(number));
        }
      }
      
      Double[] arrayVector = vector.toArray(new Double[] {});
      
      Set<String> categories = gg.googleGistsByCategory.keySet();
      
      // now we loop through each category, and for every canonical vector in
      // that category, we output the distance.
      
      for (String category : categories) {
        List<Double[]> canonicalVectors = gg.googleGistsByCategory
            .get(category);
        for (int i = 0; i < canonicalVectors.size(); ++i) {
          // get all distances
          StringBuffer allDistances = new StringBuffer();
          for (int dci = 0; dci < dcs.length; ++dci) {
            double distance = dcs[dci].distance(canonicalVectors.get(i),
                arrayVector);
            
            allDistances.append(String.valueOf(distance));
            allDistances.append(" ");
          }
          
          String keyToEmit = category + String.valueOf(i);
          
          context.write(new Text(image),
              new Text(category + " " + String.valueOf(i) + " "
                  + allDistances.toString().trim()));
        }
      }
    }
  }
  
  public static class Reduce extends Reducer<Text, Text, Text, Text> {
    
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) {
      try {
        for (Text text : values) {
          context.write(key, text);
        }
      } catch (Exception ioe) {
        System.out.println(ioe.toString());
      }
    }
  }
  
  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    conf.set("googleImages.dir", args[1]);
    
    Job job = new Job(conf, "Distance Calculator");
    
    job.setJarByClass(DistanceCalculatorMR.class);
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
    
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
    
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[2]));
    
    job.setNumReduceTasks(36);
    
    job.waitForCompletion(true);
  }
}
