import java.util.Scanner;

public class Driver {

    /**
     * The main menu of the driver function
     */
    public static void huffmanMain() {

        // Create a scanner and scan for one of the three inputs
        Scanner sc = new Scanner(System.in);
        System.out.println(
                "Welcome to the Huffman Coding encoder what would you like to do? (Type 1 to encode or 2 to decode or 3 to quit)");

        // Use a try-catch-finally to make sure we can close the scanner finally.
        try {
            // Scan inputs and sort accordingly
            String in = sc.nextLine();
            if (in.equals("1")) {
                // Encoding input
                encodingFile();
            } else if (in.equals("2")) {
                // Decoding input
                decodingFile();
            } else if (in.equals("3")) {
                // Quit input
                System.exit(0);
            } else {
                // Any other invalid input, hence we restart again
                System.out.println(in + " is an invalid input, restarting...");
                huffmanMain();
            }
        } catch (Exception e) {
            // Print any errors (None expected though)
            System.out.println(e.toString());
            huffmanMain();
        } finally {
            // Close the scanner
            sc.close();
        }
    }

    /**
     * This function is used for the decoding side of the menu
     */
    public static void decodingFile() {

        // We create a scanner and try to get the input for a file name of the input
        Scanner sc = new Scanner(System.in);
        System.out.println(
                "Please enter the file name to decode, making sure it is placed in the folder with these java files!\n     You can also type 0 to return to the main menu or 1 to quit");
        String inputFileName = sc.nextLine();

        // Check for any specific inputs for quitting and returning to the menu
        if (inputFileName.equals("0")) {
            huffmanMain();
        } else if (inputFileName.equals("1")) {
            System.exit(0);
        }

        // Get the file name for the users desired output file
        System.out.println("Please enter the file name to output too (Try make sure this is a blank file)");
        String outputFileName = sc.nextLine();

        try {
            // Create a new decoder
            HuffmanDecoder decoder = new HuffmanDecoder(inputFileName);

            // Decode the input too the output file
            decoder.decodeToFile(outputFileName);

            // Assuming no errors we allow the user to input anything and return the menu
            System.out.println("Succesfully Output to " + outputFileName + ".txt! Press enter to return to the menu!");
            sc.nextLine();
            huffmanMain();
        } catch (Exception e) {
            // Print any errors and restart the function
            System.out.println(e.toString());
            System.out.println("Error, restarting...");
            decodingFile();
        } finally {
            // Close the scanner to stem memory leaks
            sc.close();
        }
    }

    /**
     * This is used for the encoding side of the menu
     */
    public static void encodingFile() {

        // Open a scanner for the inputs and get the file name for the encoding
        Scanner sc = new Scanner(System.in);
        System.out.println(
                "Please enter the file name to encode (with the extension), making sure it is placed in the folder with these java files!\n     You can also type 0 to return to the main menu or 1 to quit");
        String fileName = sc.nextLine();

        // Check the input isn't required to move back too the menu or quit the program
        if (fileName.equals("0")) {
            huffmanMain();
        } else if (fileName.equals("1")) {
            System.exit(0);
        }

        // Run a try-catch-finally to keep the program from quitting and making sure to
        // close the scanner
        try {

            // Try and get input file
            TxtToString inputFile = new TxtToString(fileName);

            // If loaded we give the user a chance to wait before encoding and confirm it
            // with any input
            System.out.println("File loaded succesfully, ready to encode! (Press enter to continue)");
            sc.nextLine();

            // Now we encode the message using the huffman coding encoder
            HuffmanCoding encoding = new HuffmanCoding(inputFile.getContents());

            // Let the user choose the file name they want for the output
            System.out.println(
                    "File successfully encoded, please enter a file name to output this too (Make sure this file doesn't exists)");
            String outFileName = sc.nextLine();

            // Here we will try to output to a file with name specified.
            encoding.outputEncoding(outFileName);

            // Assuming we had no errors we will allow the user to return to the menu
            System.out.println("Succesfully Output to " + outFileName + "! Press enter to return to the menu!");
            sc.nextLine();
            huffmanMain();

        } catch (Exception e) {

            // If there is an error we print it as this is a tool mainly for developers so
            // no need to worry about the technical side of the error and then we restart
            // the funciton straight away
            System.out.println(e.toString());
            System.out.println("Error, restarting...");
            encodingFile();

        } finally {

            // Close the scanner to stop any potential memory leaks
            sc.close();
        }
    }

    public static void main(String[] args) {
        // We simply run the main menus function
        huffmanMain();
    }
}
