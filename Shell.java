/* $Id: Shell.java,v 1.11 2004/04/13 16:14:15 solomon Exp solomon $ */

import java.util.*;

/** A simple command-line shell for the MiniKernel.
 *
 * <p>
 * This program displays a prompt and waits for the user to enter
 * <em>command lines</em>.
 * A command line consists of one or more <em>commands</em>, separated by
 * ampersands (&amp;).
 * A command is the name of a Java class that implements
 * <samp>public static void main(String args[])</samp>, followed by
 * zero or more arguments to the command.
 * All the commands on the line are run in parallel.
 * The shell waits for them to finish before issuing another prompt.
 * <p>
 * The Shell terminates if it sees end-of-file (Control-D if input is coming
 * from the keyboard).
 * <p>
 * If this shell is invoked with any arguments, they are joined together with
 * spaces and run as a single command line.  For example,
 * <pre>
 *      java Shell Test foo "&amp; Test bar"
 * </pre>
 * is equivalent to
 * <pre>
 *      java Shell
 *      Shell&gt; Test foo &amp; Test bar
 *      Shell&gt; exit
 * </pre>
 * <p>
 * The Shell also has the following "built-in" commands.  Any arguments
 * are ignored.
 * <dl>
 * <dt><b>exit</b><dd>The Shell terminates immediately.
 * <dt><b>help</b><dd>The Shell prints a short help message.
 * <dt><b>?</b><dd>Equivalent to <b>help</b>.
 * </dl>
 * @see Kernel
 */
import java.util.StringTokenizer;

public class Shell {
    private static FileSystem fileSystem;
    /**
     * The main program.
     *
     * @param args command-line arguments. If empty, prompt the user for
     *             commands.
     */
    public static void main(String args[]) {

        FastDisk disk = new FastDisk(1024);
        fileSystem = new FileSystem(disk);
        fileSystem.initialize();
        StringBuffer sb = new StringBuffer();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(args[i]);
            }
            runCommandLine(sb.toString());
            return;
        }

        for (; ; ) {
            Library.output("Shell> ");
            int rc = Library.input(sb);

            if (rc == Kernel.ERROR_END_OF_FILE) {
                return;
            }
            if (rc < 0) {
                Library.output("Fatal error trying to read from console\n");
                System.exit(1);
            }

            if (runCommandLine(sb.toString())) {
                break;
            }
        }
    } // main(String[])

    /**
     * Help message, one line per element.
     */
    private static String[] help = {
            "usage:  Shell [ command [ & command] ... ]",
            "If no commands are specified, the Shell prompts for command lines.",
            "It terminates on end-of-file.",
            "The following commands are built in:",
            "    exit    terminate immediately",
            "    help    print this message",
            "    ?       same as help"
    };

    /**
     * Parses and runs one command line.
     *
     * @param line the command line to run.
     * @return true if the command line included an exit command.
     */
    private static boolean runCommandLine(String line) {
            // Split into commands separated by "&" for multiple commands in one line
            StringTokenizer st = new StringTokenizer(line, "&");
            boolean done = false;

            while (st.hasMoreTokens()) {
                String command = st.nextToken().trim();

                // Check for special cases
                if (command.equals("exit")) {
                    done = true;
                    break;
                }

                if (command.equals("help") || command.equals("?")) {
                    showHelp();
                    continue;
                }

                // Split the command into its parts (e.g., create, write, read, etc.)
                StringTokenizer cst = new StringTokenizer(command);
                if (!cst.hasMoreTokens()) {
                    // Empty command, skip
                    continue;
                }

                String cmd = cst.nextToken();
                switch (cmd) {
                    case "create":
                        String filename = cst.nextToken();
                        createFile(filename);
                        break;

                    case "write":
                        filename = cst.nextToken();
                        String data = cst.nextToken("\n").trim();  // Capture all data as one string
                        writeFile(filename, data);
                        break;

                    case "read":
                        filename = cst.nextToken();
                        readFile(filename);
                        break;

                    case "delete":
                        filename = cst.nextToken();
                        deleteFile(filename);
                        break;

                    case "dir":
                        listDirectory();
                        break;

                    default:
                        Library.output("Unknown command: " + cmd + "\n");
                        break;
                }
            }
            return done;
        }

        // Show help for commands
        private static void showHelp() {
            String[] help = {
                    "usage:  Shell [ command [ & command] ... ]",
                    "If no commands are specified, the Shell prompts for command lines.",
                    "It terminates on end-of-file.",
                    "The following commands are built in:",
                    "    exit    terminate immediately",
                    "    help    print this message",
                    "    create <filename>    create a new file",
                    "    write <filename> <data>    write data to a file",
                    "    read <filename>    read content from a file",
                    "    delete <filename>    delete a file",
                    "    dir    list all files in the current directory"
            };
            for (String line : help) {
                Library.output(line + "\n");
            }
        }

        // Create a file
        private static void createFile(String filename) {
            int result = fileSystem.create(filename);
            if (result == 0) {
                Library.output("Created file: " + filename + "\n");
            } else {
                Library.output("Error creating file: " + filename + "\n");
            }
        }

        // Write data to a file
        private static void writeFile(String filename, String data) {
            // Ensure the buffer size matches the disk block size (512 bytes)
            byte[] buffer = new byte[Disk.BLOCK_SIZE];
            byte[] dataBytes = data.getBytes();

            // Check if data fits in the buffer
            if (dataBytes.length > buffer.length) {
                Library.output("Error: Data is too large to fit in one block.\n");
                return;
            }

            // Copy the data into the buffer (start at index 0)
            System.arraycopy(dataBytes, 0, buffer, 0, dataBytes.length);

            int result = fileSystem.write(filename, buffer);  // This calls the write method from FileSystem
            if (result == 0) {
                Library.output("Written to file: " + filename + "\n");
            } else {
                Library.output("Error writing to file: " + filename + "\n");
            }
        }

        // Read data from a file
        private static void readFile(String filename) {
            byte[] buffer = new byte[Disk.BLOCK_SIZE];
            int result = fileSystem.read(filename, buffer);
            if (result == 0) {
                Library.output("Read from file: " + filename + " - Data: " + new String(buffer).trim() + "\n");
            } else {
                Library.output("Error reading file: " + filename + "\n");
            }
        }

        // Delete a file
        private static void deleteFile(String filename) {
            int result = fileSystem.delete(filename);
            if (result == 0) {
                Library.output("Deleted file: " + filename + "\n");
            } else {
                Library.output("Error deleting file: " + filename + "\n");
            }
        }

        // List all files in the directory
        private static void listDirectory() {
            Library.output("Listing directory:\n");
            fileSystem.dir();
        }
    }