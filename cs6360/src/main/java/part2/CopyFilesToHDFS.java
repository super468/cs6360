package part2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

// vv FileCopyWithProgress
public class CopyFilesToHDFS {
    public static void main(String[] args) throws Exception {
        // local path of directory
        String localSrc = args[0];
        // hdfs path of directory
        String dst = args[1];
        File folder = new File(localSrc);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                Configuration conf = new Configuration();
                conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
                conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));
                FileSystem fs = FileSystem.get(URI.create(dst + "/" + file.getName()), conf);
                OutputStream out = fs.create(new Path(dst + "/" + file.getName()), new Progressable() {
                    public void progress() {
                        System.out.print(".");
                    }
                });
                IOUtils.copyBytes(in, out, 4096, true);
                System.out.print("\n");
            }
        }

    }
}
// ^^ FileCopyWithProgress
