import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;

import weka.core.SparseInstance;

public class InstanceImageMap {
    private static ArrayList<String> imageIdList;
    private static HashMap<String, String> idImageMap;

    public InstanceImageMap() {
        idImageMap  = new HashMap<String, String>();
        imageIdList = new ArrayList<String>();
    }

    public static InstanceImageMap constructMap(String mapFile) {
        InstanceImageMap theMap = new InstanceImageMap();

        try {
            File file               = new File(mapFile);
            FileInputStream fis     = null;
            BufferedInputStream bis = null;
            DataInputStream dis     = null;

			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			String line = null;
			while ((line = dis.readLine()) != null) {
                String[] split  = line.split(",");
                theMap.idImageMap.put(split [0], split[1]);
            }

			fis.close();
			bis.close();
			dis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return theMap;
    }
    
    public void addImageId(String id) {
        imageIdList.add(id);
    }

    public String getImage(int index) {
        String id       = imageIdList.get(index);
        String image    = idImageMap.get(id);
        return image;
    }
}     
