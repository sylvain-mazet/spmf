package ca.pfv.spmf.test;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by smazet on 21/09/18.
 */
public class MainTestBase {
    public String fileToPath(String filename) throws UnsupportedEncodingException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());
        return file.getPath();
        //URL url = MainTestPrePostPlus.class.getResource(filename);
        //return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
