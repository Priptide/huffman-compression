import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HuffmanDecoder {
    private String fileName;

    /**
     * Create a decoder with the file name of the binary data to decode
     * 
     * @param fileName
     */
    public HuffmanDecoder(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Parse the given file into a string binary representation and then decode that
     * too the original string
     * 
     * @return Decoded string
     * @throws IOException
     */
    public String parseFile() throws IOException {

        // Find the file and create a byte array for all the bytes in the file
        File file = new File(fileName);
        byte[] bytes = new byte[(int) file.length()];

        // Create a new data input stream to read the data
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
        try {
            // Read the byte data
            dataInputStream.readFully(bytes);
            dataInputStream.close();

            // Create a string of this data
            String fullDocument = new String(bytes);

            // Split the data at the END key words
            String[] out = fullDocument.split("END");

            int overflowByteCurrent = Integer.parseInt(out[1].trim());

            // We now process the remaining data into it's binary form
            String currentBinary = returnBinaryString(bytes, overflowByteCurrent);

            // Then we try parse a set of keys
            Map<String, Character> currentEncodings = createEncodingMap(out[0]);

            // Finally send the message to be decoded
            return decodeMessage(currentBinary, currentEncodings);
        } catch (Exception e) {
            // Just return blank and we can then parse this later to be an error with the
            // input file
            return "";
        } finally {
            // Making sure to close the data stream to stop any memory leaks
            dataInputStream.close();
        }
    }

    /**
     * This is used to take the byte data and create a binary representation, this
     * will include the encodings and hence needs to be sorted later
     * 
     * @param data
     * @param byteBuffer
     * @return Binary string of 0's and 1's of the given data
     */
    public String returnBinaryString(byte[] data, int byteBuffer) {

        // Take the input string and create a new byte array
        StringBuilder bld = new StringBuilder();
        for (byte b : data) {
            // Formary the byte data to a binary string of 0's and 1's
            bld.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        String output = bld.toString();

        // We then remove the last 0's that are added as part of the byte buffer
        return output.substring(0, output.length() - byteBuffer);
    }

    /**
     * Create an encoding map for all the current character encoding which are in
     * the header of the file
     * 
     * @param currentData
     * @return Map of encodings and their given characters
     */
    public Map<String, Character> createEncodingMap(String currentData) {

        // Create a new map to hold the encodings and their characters
        Map<String, Character> currentMap = new HashMap<>();

        // Split at the Nx key word to split all the encodings
        String[] stringEncoding = currentData.split("Nx");

        // Loop through all these encodings
        for (String s : stringEncoding) {
            // Check our length is long enough to be an encoding string
            if (s.length() > 2) {
                // Split the string to the character and encoding
                String[] localStrings = s.split("--");
                // Create the character value and asign that to the binary key
                char localChar = localStrings[0].toCharArray()[1];
                currentMap.put(localStrings[1].trim(), localChar);
            }
        }

        // Return this formatted map
        return currentMap;
    }

    /**
     * This will take the long binary file and the current encodings for characters
     * and output the decoded text as a string, removing all the encoding headers
     * aswell
     * 
     * @param encodedMessage
     * @param currentEncoding
     * @return The string representation of the message
     */
    public String decodeMessage(String encodedMessage, Map<String, Character> currentEncoding) {

        // Create a blank string for the output message
        StringBuilder currentMessage = new StringBuilder();

        // Create a string builder for encoding
        StringBuilder builderEncoding = new StringBuilder();

        // Iterate through the string and check for it's exsistance in the key list
        for (int i = 0; i < encodedMessage.length(); i++) {

            // Add the binary 0 or 1 to the current check string
            char localChar = encodedMessage.charAt(i);
            builderEncoding.append(localChar);

            // Check if this string is a character encoding
            if (currentEncoding.containsKey(builderEncoding.toString())) {
                // If so we add this character to our representation of the current message
                currentMessage.append(currentEncoding.get(builderEncoding.toString()));
                // Then set the current check back to a blank string
                builderEncoding = new StringBuilder();
            }

        }

        // Here we vet the message to remove the header
        int index = 0;

        // Split the start at the given start flag to remove
        // any text before this (the header)
        StringBuilder bld = new StringBuilder();
        for (String s : currentMessage.toString().split("start ")) {
            if (index < 1) {
                // Remove the first part of the string (the header data)
                index += 1;
            } else {
                // Add any other strings left to the output, this is in case the word start is
                // in the string
                bld.append(s);
            }
        }

        String output = bld.toString();

        // Return the formatted string
        return output;

    }

    /**
     * Will decode an encoded file to a text document with the given file name
     * 
     * @param outputFileName
     * @throws IOException
     */
    public void decodeToFile(String outputFileName) throws IOException {

        // Parse the file to give the output string
        String outputForFile = parseFile();

        // This will only happen if the file is invalid or empty and hence also invalid
        // so we throw an error
        if (outputForFile.equals("")) {
            throw new IOException("Error, the input file is invalid");
        }

        // Create an output file and write the string too this file
        try (PrintWriter out = new PrintWriter(outputFileName + ".txt")) {
            out.println(outputForFile);
        }

    }

}
