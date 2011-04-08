import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.Mapper;

public class GistCalculatorMR {

  private static class GistCalculatorMapper extends
      Mapper<Object, Text, Text, Text> implements Configurable {
    private Configuration conf;
    private GistCalculator gCalc;

    @Override
    public void setup(Context context) {
      gCalc = new GistCalculator();
    }

    public void map(Object key, Text value, Context context) {
      String filename = value.toString().trim();
      Path filePath = new Path(filename);
      String basename = filePath.getName();
      String dirname = filePath.getParent().toString(); 
      FileSystem fs = null;
      try {
        fs = FileSystem.get(conf);
      } catch (IOException e) {
        return;
      }

      FSDataInputStream imageStream;
      float[] gistValues = null;
      StringBuilder sb = new StringBuilder();
      try {
        imageStream = fs.open(filePath);
        gistValues = gCalc.getGist(imageStream);
        for (float gistValue : gistValues) {
          sb.append(gistValue + ",");
        }
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      
      try {
        context.write(new Text(filename), new Text(sb.toString()));
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    @Override
    public Configuration getConf() {
      return conf;
    }

    @Override
    public void setConf(Configuration conf) {
      this.conf = conf;
    }
  }

  public static void main(String[] args) throws Exception {
    String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
    if (otherArgs.length != 3) {
      System.out.println("Usage: <program> <input> <output> <gistlib-path>");
      System.exit(-1);
    }

    Configuration conf = new Configuration();
      DistributedCache.createSymlink(conf);
      DistributedCache.addCacheFile(new URI(otherArgs[2]), conf);
      
    Job job = new Job(conf, "Gist Calculator MR");

    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    job.setJarByClass(GistCalculatorMR.class);
    job.setMapperClass(GistCalculatorMR.GistCalculatorMapper.class);
    job.setNumReduceTasks(0);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    job.waitForCompletion(true);

    System.exit(0);
  }
}
