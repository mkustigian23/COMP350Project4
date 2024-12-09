    /* $Id: FastDisk.java,v 1.15 2006/11/22 21:47:00 solomon Exp $ */

    import java.io.*;
    import java.util.StringTokenizer;

    import static java.lang.System.out;

    /** A new and improved Disk.
     * <b>You may not change this class.</b>
     * <p>
     * This disk is so much faster than the previous model that read and write
     * operations appear to finish in no time.   Because disk is so fast, beginRead
     * and beginWrite wait for the operation to finish rather than causing a CPU
     * interrupt when they complete.
     * <p>
     * @see Disk
     * @see Kernel
     */
public class FileTester {
    private static FileSystem fileSystem;

    public static void main(String[] args) {
            // Initialize the file system
            FastDisk disk = new FastDisk(1024);  // Assuming a disk size of 1024 blocks
            fileSystem = new FileSystem(disk);
            fileSystem.initialize();  // Format and initialize the file system

            // Test commands
            runCommands();
        }
        private static void runCommands() {
            StringBuffer sb = new StringBuffer();

            // Example commands to test file system
            String[] commands = {
                    "create file1.txt",
                    "write file1.txt Hello, world!",
                    "read file1.txt",
                    "create file2.txt",
                    "dir",
                    "delete file1.txt",
                    "dir"
            };

            for (String command : commands) {
                Library.output("Command: " + command + "\n");
                StringTokenizer st = new StringTokenizer(command);
                String cmd = st.nextToken();
                if (cmd.equals("create")) {
                    String filename = st.nextToken();
                    createFile(filename);
                } else if (cmd.equals("write")) {
                    String filename = st.nextToken();
                    String data = st.nextToken("\n").trim();
                    writeFile(filename, data);
                } else if (cmd.equals("read")) {
                    String filename = st.nextToken();
                    readFile(filename);
                } else if (cmd.equals("delete")) {
                    String filename = st.nextToken();
                    deleteFile(filename);
                } else if (cmd.equals("dir")) {
                    listDirectory();
                }
            }
        }

        private static void createFile(String filename) {
            int result = fileSystem.create(filename);
            if (result == 0) {
                Library.output("Created file: " + filename + "\n");
            } else {
                Library.output("Error creating file: " + filename + "\n");
            }
        }

        private static void writeFile(String filename, String data) {
            byte[] buffer = data.getBytes();
            int result = fileSystem.write(filename, buffer);
            if (result == 0) {
                Library.output("Written to file: " + filename + "\n");
            } else {
                Library.output("Error writing to file: " + filename + "\n");
            }
        }

        private static void readFile(String filename) {
            byte[] buffer = new byte[Disk.BLOCK_SIZE];
            int result = fileSystem.read(filename, buffer);
            if (result == 0) {
                Library.output("Read from file: " + filename + " - Data: " + new String(buffer).trim() + "\n");
            } else {
                Library.output("Error reading file: " + filename + "\n");
            }
        }

        private static void deleteFile(String filename) {
            int result = fileSystem.delete(filename);
            if (result == 0) {
                Library.output("Deleted file: " + filename + "\n");
            } else {
                Library.output("Error deleting file: " + filename + "\n");
            }
        }

        private static void listDirectory() {
            Library.output("Listing directory:\n");
            fileSystem.dir();
        }
    }