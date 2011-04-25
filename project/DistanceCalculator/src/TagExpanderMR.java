import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.didion.jwnl.JWNLException;

import org.apache.hadoop.conf.Configuration;
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
import org.apache.hadoop.util.StringUtils;

public class TagExpanderMR {

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		Configuration conf = null;
		WordnetUtil wn = null;

		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
		}		
		
		@Override
		public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException {
			if(wn == null) {
				try {
					wn = WordnetUtil.getInstance();
				} catch (JWNLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					throw new IOException("Could not initialize wordnet util.");
				}
			}
			
			String line = value.toString();
			String [] parts = line.split("[\\s]+]");
			
			String id = parts[0],
			secret = parts[1],
			owner = parts[3],
			joined_tags = parts[5];
			
			String [] originalTags = joined_tags.split("\\|");
			
			Set<String> expandedTags = new HashSet<String>();			
			
			for(String originalTag : originalTags) {
				try {
					expandedTags.addAll(wn.expandWord(originalTag));
				} catch (JWNLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			String uniqKey = id + "|" + secret + "|" + owner;
			
			for (String tag : originalTags) {
				context.write(new Text(uniqKey), new Text(tag));
				context.write(new Text("__ALL__"), new Text(tag));
			}
			
			for (String tag : expandedTags) {
				context.write(new Text(uniqKey), new Text(tag));
				context.write(new Text("__ALL__"), new Text(tag));
			}
		}
	}
	
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		Configuration conf = null;

		@Override
		public void setup(Context context) {
			conf = context.getConfiguration();
		}	
		
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) {
			Set<String> tags = new HashSet<String>();
			
			for(Text tag : values) {
				tags.add(tag.toString());
			}
			
			try {
				context.write(key, new Text(join(tags, ",")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String join( Iterable< ? extends Object > pColl, String separator )
    {
        Iterator< ? extends Object > oIter;
        if ( pColl == null || ( !( oIter = pColl.iterator() ).hasNext() ) )
            return "";
        StringBuilder oBuilder = new StringBuilder( String.valueOf( oIter.next() ) );
        while ( oIter.hasNext() )
            oBuilder.append( separator ).append( oIter.next() );
        return oBuilder.toString();
    }
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		Job job = new Job(conf, "Tag Expander");

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
