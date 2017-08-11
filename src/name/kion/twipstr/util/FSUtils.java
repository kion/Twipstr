/**
 * Created on Aug 10, 2017
 */
package name.kion.twipstr.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author kion
 */
public class FSUtils {

    private FSUtils() {
        // hidden default constructor
    }

    public static void writeFile(File file, String content) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }

}
