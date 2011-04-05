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

public class DownloadImages {

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		private String inputFile;
		private Configuration conf;
		private String outputDir;

		@Override
			public void setup(Context context) {
				inputFile = ((FileSplit) context.getInputSplit()).getPath().getName();
				conf = context.getConfiguration();
				outputDir = conf.get("output.dir");
			}

		@Override
			public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

				String line = value.toString();

				int lastSpace = line.lastIndexOf(" ");
				String meta = line.substring(0, lastSpace);
				String url = line.substring(lastSpace + 1);

				context.write(new Text(meta), new Text(url));
			}
	} 

	public static class Reduce extends Reducer<Text, Text, Text, Text> {


		private Configuration conf;
		private String outputDir;

		@Override
			public void setup(Context context) {
				conf = context.getConfiguration();
				outputDir = conf.get("output.dir");
			}

		@Override
			public void reduce(Text key, Iterable<Text> values, Context _context) 
			throws IOException, InterruptedException {

				final FileSystem fs = FileSystem.get(conf);
				final String meta = key.toString();
				Iterator<Text> it = values.iterator();
				final String url = it.next().toString();


				try {
					String suffix = "b";
					String fileName = meta.replaceAll(" ", "_") + ".jpg";
					Path p = new Path(outputDir + "/" + suffix  + "_" + fileName);

					if(fs.exists(p)) {
						return;
					}



					boolean failed = false;
					URL flickr = new URL(url.replace("_o.jpg", "_b.jpg"));
					InputStream in = flickr.openStream();
					ByteArrayOutputStream mem = new ByteArrayOutputStream();

					int bytesRead;
					byte [] buffer = new byte[4096*1024];
					while ((bytesRead = in.read(buffer)) > 0) {
						mem.write(buffer, 0, bytesRead);
					}

					if(mem.size() < 5 * 1024) {
						suffix = "b";
						mem = new ByteArrayOutputStream();
						flickr = new URL(url);
						in = flickr.openStream();
						while ((bytesRead = in.read(buffer)) > 0) {
							mem.write(buffer, 0, bytesRead);
						}
					}

					if(mem.size() < 5 * 1024) {
						failed = true;
					}

					if(!failed) {
						buffer = mem.toByteArray();
						FSDataOutputStream out = fs.create(p);

						out.write(buffer, 0, buffer.length);
						out.close();
					}
				}
				catch(Exception e) {
				} 
			}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("output.dir", args[1]);

		Job job = new Job(conf, "Image Downloader");

		job.setJarByClass(DownloadImages.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));

		job.setNumReduceTasks(36);

		job.waitForCompletion(true);
	}

}
