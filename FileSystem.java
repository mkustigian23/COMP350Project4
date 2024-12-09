
    import java.util.Arrays;

    public class FileSystem {
        private static final int BLOCK_SIZE = Disk.BLOCK_SIZE;
        private static final int FREE_MAP_BLOCKS = 14; // Reserved for free map
        private static final int DISK_SIZE = 1024; // Number of blocks

        private final Disk disk;
        private boolean[] freeMap; // Tracks free/used blocks
        private String[] fileTable; // Maps block indices to filenames

        public FileSystem(Disk disk) {
            this.disk = disk;
            this.freeMap = new boolean[DISK_SIZE];
            this.fileTable = new String[DISK_SIZE];
        }

        /** Initializes the file system. */
        public void initialize() {
            Arrays.fill(freeMap, false);
            Arrays.fill(fileTable, null);
            Library.output("FileSystem: Initialized.\n");
        }

        /** Formats the disk and clears the file system state. */
        public int format() {
            try {
                Arrays.fill(freeMap, false);
                Arrays.fill(fileTable, null);

                byte[] emptyBlock = new byte[BLOCK_SIZE];
                for (int i = 0; i < FREE_MAP_BLOCKS; i++) {
                    disk.write(i, emptyBlock);
                }
                Library.output("FileSystem: Disk formatted.\n");
                return 0; // Success
            } catch (Exception e) {
                Library.output("Error formatting disk: " + e.getMessage() + "\n");
                return Kernel.ERROR_IO;
            }
        }

        /** Creates a new file. */
        public int create(String filename) {
            if (findFile(filename) != -1) {
                Library.output("Error: File already exists.\n");
                return Kernel.ERROR_BAD_ARGUMENT;
            }

            for (int i = FREE_MAP_BLOCKS; i < DISK_SIZE; i++) {
                if (!freeMap[i]) {
                    freeMap[i] = true;
                    fileTable[i] = filename;
                    Library.output("FileSystem: File created: " + filename + "\n");
                    return 0;
                }
            }
            Library.output("Error: Disk is full.\n");
            return Kernel.ERROR_OUT_OF_RANGE;
        }

        /** Writes data to a file. */
        public int write(String filename, byte[] buffer) {
            int blockIndex = findFile(filename);
            if (blockIndex == -1) {
                Library.output("Error: File not found.\n");
                return Kernel.ERROR_BAD_ARGUMENT;
            }

            try {
                disk.write(blockIndex, buffer);
                Library.output("FileSystem: Data written to file: " + filename + "\n");
                return 0;
            } catch (Exception e) {
                Library.output("Error writing to file: " + e.getMessage() + "\n");
                return Kernel.ERROR_IO;
            }
        }

        /** Reads data from a file. */
        public int read(String filename, byte[] buffer) {
            int blockIndex = findFile(filename);
            if (blockIndex == -1) {
                Library.output("Error: File not found.\n");
                return Kernel.ERROR_BAD_ARGUMENT;
            }

            try {
                disk.read(blockIndex, buffer);
                Library.output("FileSystem: Data read from file: " + filename + "\n");
                return 0;
            } catch (Exception e) {
                Library.output("Error reading file: " + e.getMessage() + "\n");
                return Kernel.ERROR_IO;
            }
        }

        /** Deletes a file. */
        public int delete(String filename) {
            int blockIndex = findFile(filename);
            if (blockIndex == -1) {
                Library.output("Error: File not found.\n");
                return Kernel.ERROR_BAD_ARGUMENT;
            }

            freeMap[blockIndex] = false;
            fileTable[blockIndex] = null;

            byte[] emptyBlock = new byte[BLOCK_SIZE];
            try {
                disk.write(blockIndex, emptyBlock);
                Library.output("FileSystem: File deleted: " + filename + "\n");
                return 0;
            } catch (Exception e) {
                Library.output("Error deleting file: " + e.getMessage() + "\n");
                return Kernel.ERROR_IO;
            }
        }

        /** Displays the directory contents. */
        public int dir() {
            Library.output("Directory:\n");
            for (int i = FREE_MAP_BLOCKS; i < DISK_SIZE; i++) {
                if (fileTable[i] != null) {
                    Library.output(fileTable[i] + "\n");
                }
            }
            return 0;
        }

        /** Finds the block index of a file. */
        private int findFile(String filename) {
            for (int i = FREE_MAP_BLOCKS; i < DISK_SIZE; i++) {
                if (filename.equals(fileTable[i])) {
                    return i;
                }
            }
            return -1;
        }
    }

