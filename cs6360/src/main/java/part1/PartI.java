package part1;

import java.io.*;
import java.net.URL;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.Progressable;

public class PartI {

//    static String getFilename(String url){
//        return ("".equals(url) || null == url) ? "" : url.substring(url.lastIndexOf("/") + 1,url.length());
//    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String hdfs_address = args[0];
        String[] urlList = {"http://www.utdallas.edu/~axn112530/cs6350/lab2/input/20417.txt.bz2","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/5000-8.txt.bz2","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/132.txt.bz2","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/1661-8.txt.bz2","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/972.txt.bz2","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/19699.txt.bz2"};
        InputStream in = null;
        FSDataOutputStream out = null;

        for(String url:urlList){
            String[] fileNameTemp = url.split("/");
            String fileName = fileNameTemp[fileNameTemp.length - 1];

            try {
                in = new URL(url).openStream();
                String uri = hdfs_address + "/" + fileName;
                FileSystem fs = FileSystem.get(URI.create(uri), conf);
                Path inputPath = new Path(uri);
                out = fs.create(inputPath, new Progressable() {
                    public void progress() {
                        System.out.print(".");
                    }
                });
                IOUtils.copyBytes(in,out,4096,true);

                CompressionCodecFactory factory = new CompressionCodecFactory(conf);
                CompressionCodec codec = factory.getCodec(inputPath);
                if (codec == null) {
                    System.err.println("No codec found for" + uri);
                    System.exit(1);
                }

                String outputUri = CompressionCodecFactory.removeSuffix(uri, codec.getDefaultExtension());
                try{
                    in = codec.createInputStream(fs.open(inputPath));
                    out = fs.create(new Path(outputUri));
                    IOUtils.copyBytes(in, out, conf);
                }catch (Exception e){
                    e.printStackTrace();
                }
                fs.deleteOnExit(inputPath);

            }finally{
                IOUtils.closeStream(in);
                IOUtils.closeStream(out);
            }
        }

    }

}
