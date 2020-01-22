package nio_file_copy;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

public class FileCopyDemo {
    public static final int ROUND = 5;

    public static void run(FileCopyRunner test, File source, File target) {
        long sum = 0L;
        for (int i = 0; i < ROUND; i++) {
            long begin = System.currentTimeMillis();
            test.copyFile(source, target);
            long end = System.currentTimeMillis();
            sum += end - begin;
        }
        System.out.println(test.toString() + ":" + sum / ROUND);
    }

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "FileCopyRunner";
            }

            @Override
            public void copyFile(File source, File target) {
                FileInputStream fin = null;
                FileOutputStream fout = null;
                try {
                    fin = new FileInputStream(source);
                    fout = new FileOutputStream(target);

                    int n;
                    while (true) {
                        //为什么FileInputStream 也能read(byte[])呢;
                        if (!((n = fin.read()) != -1)) break;
                        fout.write(n);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        FileCopyRunner bufferStreamCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "FileCopyRunner";
            }

            @Override
            public void copyFile(File source, File target) {
                BufferedInputStream bin = null;
                BufferedOutputStream bout = null;
                try {
                    bin = new BufferedInputStream(new FileInputStream(source));
                    bout = new BufferedOutputStream(new FileOutputStream(target));
                    byte[] buffer = new byte[1024];
                    int len;
                    while (true) {
                        try {
                            if (!((len = bin.read(buffer)) != -1)) break;
                            bout.write(buffer, 0, len);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    close(bin);
                    close(bout);
                }
            }
        };
        //通过缓冲区
        FileCopyRunner nioBufferCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "FileCopyRunner";
            }

            @Override
            public void copyFile(File source, File target) {
                //还有SocketChannel  SocketServerChannel
                FileChannel fin = null;
                FileChannel fout = null;
                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len;
                    while ((len = fin.read(byteBuffer)) != -1) {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining()) {
                            fout.write(byteBuffer);
                        }
                        byteBuffer.clear();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }
        };

        //直接通道转发
        FileCopyRunner bioTransferCopy = new FileCopyRunner() {
            @Override
            public String toString() {
                return "FileCopyRunner";
            }

            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;

                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    long transsize = 0;
                    while (transsize != fin.size()) {
                        transsize += fin.transferTo(0, fin.size(), fout);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }

            }
        };

    }
}
