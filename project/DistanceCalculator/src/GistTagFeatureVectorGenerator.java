import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class GistTagFeatureVectorGenerator.
 */
public class GistTagFeatureVectorGenerator {
	
	/**
	 * The Class Map.
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		
		/** The conf. */
		Configuration conf = null;

		/* (non-Javadoc)
		 * @see org.apache.hadoop.mapreduce.Mapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
		 */
		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
		}

		/* (non-Javadoc)
		 * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)
		 */
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();

			String[] parts = line.split("[\\s]+");

			String identifier = parts[0];

			if(parts.length != 2) {
				System.out.println("Perhaps no tags for " + identifier.toString());
				return;
			}
			
			Text tUniqKey;
			String valueId;

			if (identifier.startsWith("/user/rmenon")) {
				// /user/rmenon/wap/tagpander/images/harley/b_harley.txt_976604276_edc2679a53_1069_9426538@N07.jpg
				String[] pathParts = identifier.split("[\\_]");
				int count = pathParts.length;

				// last part
				String owner = pathParts[count - 1];
				// remove the extension
				owner = owner.substring(0, owner.indexOf("."));

				// third last part
				String secret = pathParts[count - 3];

				// fourth last part
				String id = pathParts[count - 4];

				tUniqKey = new Text(id + "|" + secret + "|" + owner);

				valueId = "gist:";

				System.out.println("gist key : " + tUniqKey.toString());
			} else {
				tUniqKey = new Text(parts[0]);
				System.out.println("tag key  : " + tUniqKey.toString());
				valueId = "tag:";
			}

			context.write(tUniqKey, new Text(valueId + parts[1].toString()));
		}
	}

	/**
	 * The Class Reduce.
	 */
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		
		/**
		 * The Class Data.
		 */
		private class Data {
			
			/** The tags. */
			Set<String> tags;
			
			/** The gists. */
			String gists;
		}

		/** The conf. */
		Configuration conf = null;

		/* (non-Javadoc)
		 * @see org.apache.hadoop.mapreduce.Reducer#setup(org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
		}

		/* (non-Javadoc)
		 * @see org.apache.hadoop.mapreduce.Reducer#reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) {
			Data data = new Data();

			int count = 0;
			for (Text value : values) {
				getData(data, value.toString());
				count += 1;
			}

			if (data.gists == null || data.tags == null) {
				System.out.println("Data mismatch for " + key.toString());
				return;
			}

			for (String tag : data.tags) {
				Set<String> tagsCopy = new HashSet<String>();
				tagsCopy.addAll(data.tags);

				tagsCopy.remove(tag);

				String tagVector = join(tagsCopy, ",");

				try {
					String vector = data.gists + "," + tagVector;

					// not sure why this is needed, but let's just make things
					// saner for the next guy down the line
					vector = vector.replace(",,", ",");

					context.write(new Text(key.toString() + "|" + tag),
							new Text(vector));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					throw new Error(e);
				}
			}

		}

		/**
		 * Gets the data.
		 *
		 * @param data the data
		 * @param line the line
		 * @return the data
		 */
		void getData(Data data, String line) {
			if (line.startsWith("tag:")) {
				String[] tags = line.substring(line.indexOf(":") + 1).split(
						"[\\s,]+");

				Set<String> tagSet = new HashSet<String>();

				for (String tag : tags) {
					tagSet.add(tag);
				}

				data.tags = tagSet;
			}

			if (line.startsWith("gist:")) {
				data.gists = line.substring(line.indexOf(":") + 1);
			}
		}

		/**
		 * Join.
		 *
		 * @param pColl the coll
		 * @param separator the separator
		 * @return the string
		 */
		public static String join(Iterable<? extends Object> pColl,
				String separator) {
			Iterator<? extends Object> oIter;
			if (pColl == null || (!(oIter = pColl.iterator()).hasNext()))
				return "";
			StringBuilder oBuilder = new StringBuilder(String.valueOf(oIter
					.next()));
			while (oIter.hasNext())
				oBuilder.append(separator).append(oIter.next());
			return oBuilder.toString();
		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		/*
		 * Input : flickr gist and expanded tags Output : joined stuff
		 */

		if (args.length != 3) {
			System.err.println("Usage: program flickr_gists tags outputpath");
			return;
		}

		Configuration conf = new Configuration();

		Job job = new Job(conf, "GistTagFeatureVectorGenerator");

		job.setJarByClass(DistanceCalculatorMR.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		job.setNumReduceTasks(36);

		job.waitForCompletion(true);
	}

}
