import java.io.Writer;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class HTMLHandler {
    private Writer htmlWriter;
    private int id;

    public HTMLHandler(String htmlFile) {
        try {
            htmlWriter  = new OutputStreamWriter(new FileOutputStream(htmlFile));
            id          = 0;
            insertPrologue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            insertEpilogue();
            htmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertPrologue() {

        try {
            htmlWriter.write("<html>");
            htmlWriter.write("\n<head>");
            htmlWriter.write("\n\t<script language=\"javascript\">");
            htmlWriter.write("\n\t\tvar homeUrl=\"http://localhost/images/\";");
            htmlWriter.write("\n\t\tfunction getUrl(imageName) {");
            htmlWriter.write("\n\t\t\treturn homeUrl+imageName;");
            htmlWriter.write("\n\t\t}");
            htmlWriter.write("\n\t</script>");
            htmlWriter.write("\n</head>");
            htmlWriter.write("\n<body>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertEpilogue() {
        try {
            htmlWriter.write("\n</body>");
            htmlWriter.write("\n</html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addImage(String imageName, String captions) {
        String[] captionList   = captions.split("\t");
        try {
            htmlWriter.write("\n\t<img src=\"test.png\" id=\"image" + id + "\">");
            for (int i = 0; i < captionList.length; i++) {
                htmlWriter.write("\n\t<br>" + captionList [i]);
            }

            htmlWriter.write("\n\t<script type=\"text/javascript\">");
            htmlWriter.write("\n\t\tdocument.getElementById('image" + id + "').src = getUrl(\""+ imageName + "\");");
            htmlWriter.write("\n\t</script>");
            id ++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
