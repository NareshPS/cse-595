import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class FeatVectorByCategoryMR {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		Configuration conf = null;
		WordnetUtil wn = null;
		java.util.Map<String, Boolean> categoryIdentifiers;

		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			if (null == categoryIdentifiers) {
				try {
					loadCategories();
				} catch (IOException e) {
					throw new Error(e);
				}
			}

			String line = value.toString();
			String[] parts = line.split("[\\s]+");
			String id = parts[0];
			id = id.substring(0, id.lastIndexOf("|"));

			if (categoryIdentifiers.containsKey(id)) {
				context.write(new Text(parts[0]), new Text(parts[1]));
			}
		}

		private void loadCategories() throws IOException {
			categoryIdentifiers = new HashMap<String, Boolean>();

			String category = conf.get("category.name");

			FileSystem fs = FileSystem.get(conf);

			for (FileStatus status : fs.listStatus(new Path(conf
					.get("category.listing.directory")))) {
				Path dataFilePath = status.getPath();

				if (!fs.isFile(dataFilePath))
					continue;

				Scanner scanner = new Scanner(fs.open(dataFilePath));

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().trim();

					String[] parts = line.split("[\\s]+");

					if (parts[0].equals(category)) {
						String[] pathParts = parts[1].split("[\\_]");
						int count = pathParts.length;

						// last part
						String owner = pathParts[count - 1];
						// remove the extension
						owner = owner.substring(0, owner.indexOf("."));

						// third last part
						String secret = pathParts[count - 3];

						// fourth last part
						String id = pathParts[count - 4];

						categoryIdentifiers.put(
								id + "|" + secret + "|" + owner, true);
					}
				}
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		conf.set("category.listing.directory", args[2]);
		conf.set("category.name", args[3]);

		Job job = new Job(conf, "FeatVectorByCategory");

		job.setJarByClass(DistanceCalculatorMR.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setNumReduceTasks(36);

		job.waitForCompletion(true);
	}

}
