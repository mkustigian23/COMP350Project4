
/**
 * Utilities.java
 * Static class for maintaining pack, unpack
 * other file sytem utilities
 *
 */
public class Utilities
{
    /**
     * Store a 16-bit integer into a byte array.
     * @param n         the integer to be stored
     * @param buf       the byte array into which it should be stored
     * @param offset    the index of the first byte to be modified
     */
    public static void pack(short n, byte[] buf, int offset)
    {
        buf[offset] = (byte) n;
        buf[offset + 1] = (byte) (n >> 8);
    }

    /**
     * Convert a field in a byte array to a 16-bit integer.
     * @param buf       the byte array containing the data.
     * @param offset    the location in the array where the data starts.
     * @return          the short integer value.
     */
    public static short unpackShort(byte[] buf, int offset)
    {
        return (short) ((buf[offset] & 0xff) + ((buf[offset + 1] & 0xff) << 8));
    }

    /**
     * Store a 32-bit integer into a byte array.
     * @param n         the integer to be stored
     * @param buf       the byte array into which it should be stored
     * @param offset    the index of the first byte to be modified
     */
    public static void pack(int n, byte[] buf, int offset)
    {
        buf[offset] = (byte) (n >> 0);
        buf[offset + 1] = (byte) (n >> 8);
        buf[offset + 2] = (byte) (n >> 16);
        buf[offset + 3] = (byte) (n >> 24);
    }

    /**
     * Convert a field in a byte array to a 32-bit integer.
     * @param buf       the byte array containing the data.
     * @param offset    the location in the array where the data starts.
     * @return          the integer value.
     */
    public static int unpackInt(byte[] buf, int offset)
    {
        return ((buf[offset] & 0xff) + ((buf[offset + 1] & 0xff) << 8) +
                ((buf[offset + 2] & 0xff) << 16) + ((buf[offset + 3] & 0xff) <<
                24));
    }

    /**
     * Store an array of boolean values into a byte array.
     * @param bits      the array of boolean values to be stored
     * @param buf       the byte array into which it should be stored
     * @param offset    the index of the first byte to be modified
     */
    public static void pack(boolean[] bits, byte[] buf, int offset)
    {
        for ( int i = offset; i < offset + bits.length / 8 + 1; i ++ )
        {
            buf[i] = (byte) 0;
        }
        for ( int i = 0; i < bits.length; i ++ )
        {
            if ( bits[i] )
            {
                buf[offset + i / 8] |= 1 << (8 - (i % 8) - 1);
            }
        }
    }

    /**
     * Convert a field in a byte array to an array of boolean values.
     * @param buf       the byte array containing the data.
     * @param offset    the location in the array where the data starts.
     * @param size      the size of the array to be extracted from the buffer
     * @return          the array of boolean values.
     */
    public static boolean[] unpackArrayBool(byte[] buf, int offset, int size)
    {
        boolean[] bits = new boolean[size];
        for ( int i = 0; i < size; i ++ )
        {
            if ( (buf[(offset + i / 8)] & (1 << (8 - (i % 8) - 1))) > 0 )
            {
                bits[i] = true;
            }

        }
        return bits;
    }

    /**
     * Store a String into a byte array.
     * @param text      the String to be stored
     * @param buf       the byte array into which it should be stored
     * @param offset    the index of the first byte to be modified
     */
    public static void pack(String text, byte[] buf, int offset)
    {
        byte[] bytes = text.getBytes();
        for ( int i = 0; i < bytes.length; i ++ )
        {
            buf[offset + i] = bytes[i];
        }
    }

    /**
     * Convert a field in a byte array to a String.
     * @param buf       the byte array containing the data.
     * @param offset    the location in the array where the data starts.
     * @return          the String value.
     */
    public static String unpackString(byte[] buf, int offset)
    {
        return new String(buf, offset, buf.length - offset - 1);
    }

    /**
     * Returns a string containing the given string repeated
     * the given number of times
     * @param string    the string to be repeated
     * @param count     the number of times the string should be repeated
     * @return          A string value containing "count" copies of "string"
     */
    public static String strRepeat(String string, int count)
    {
        String result = "";
        for ( int i = 0; i < count; i ++ )
        {
            result = result + string;
        }
        return result;
    }

    /**
     * Converts a boolean array to a string of 1's and 0's
     * @param bits    the boolean array to be converted
     * @return        A string containing a 1 for each true
     *                value and a 0 for each false value in bits
     */
    public static String boolToStr(boolean[] bits)
    {
        String text = "";
        for ( int i = 0; i < bits.length; i ++ )
        {
            if ( bits[i] )
            {
                text += "1";
            }
            else
            {
                text += "0";
            }
        }
        return text;
    }
}
