import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HuffmanCoding {

    // Variables
    private String inputString;

    private Map<Character, Integer> inputSplit;

    private Map<Integer, List<Character>> currentPositions;

    private Map<Character, String> characterEncoding;

    private Node rootNode;

    private int overflowByte;

    // Constructor

    /**
     * Create a Huffman Coding object for encoding text files
     */
    public HuffmanCoding(String input) {
        this.inputString = input;
        this.inputSplit = new HashMap<>();
        this.currentPositions = new TreeMap<>();
        rootNode = null;
        overflowByte = 0;

        // We already sort the input when it is recieved
        splitString();
    }

    // Methods

    /**
     * Used to split the string into a map, only use once or it will create issues
     */
    public void splitString() {

        // Iterate through the string
        for (int i = 0; i < inputString.length(); i++) {
            char localChar = inputString.charAt(i);

            // If the character as a key already exsists add one too it's count
            if (inputSplit.containsKey(localChar)) {
                int currentValue = inputSplit.get(localChar);
                currentValue += 1;
                inputSplit.put(localChar, currentValue);
            } else {
                // If not create a new key for the character
                inputSplit.put(localChar, 1);
            }

        }

        // Now we update the character values so it can be sorted
        updatePositions();
    }

    /**
     * Used to update the positions and values of characters into a hash map of
     * character count and then lists of characters with the given count
     */
    public void updatePositions() {

        // Sort every character from the original hash map
        for (char localChar : inputSplit.keySet()) {
            int currentValue = inputSplit.get(localChar);
            // Place the character in a list at it's current value
            if (currentPositions.containsKey(currentValue)) {
                // If there is already a character at this value we update the list to add this
                // character and update the hash map
                List<Character> localList = currentPositions.get(currentValue);
                localList.add(localChar);
                currentPositions.put(currentValue, localList);
            } else {
                // If there isn't any characters there we create a new list and add our
                // character to it then add that too the hash map
                List<Character> localList = new ArrayList<>();
                localList.add(localChar);
                currentPositions.put(currentValue, localList);
            }
        }

        // We now create the binary tree
        createTree();
    }

    /**
     * Uses the Node object to create a binary tree for the Huffman Coding
     */
    public void createTree() {

        // Create a new node list (should update this to a queue or stack)
        List<Node> currentNodes = new ArrayList<>();

        // Loop through all the characters and create nodes for them
        currentPositions.entrySet().forEach(entry -> {
            for (char localChar : entry.getValue()) {
                Node currentNode = new Node();
                currentNode.character = localChar;
                currentNode.count = entry.getKey();
                currentNode.leftNode = null;
                currentNode.rightNode = null;
                // Mark these nodes as characters
                currentNode.isCharNode = true;
                currentNodes.add(currentNode);
            }
        });

        // Iterate through the nodes in the list until only one root node is left
        while (currentNodes.size() > 1) {

            // Get the two lowest nodes
            Node[] nodes = getLowestNodes(currentNodes);

            // Create a new parent node for the two smaller nodes
            Node combined = new Node();
            // Add the child nodes to this nodes children
            combined.leftNode = nodes[0];
            combined.rightNode = nodes[1];

            // Give this node the value of the two child nodes count combined
            combined.count = nodes[0].count + nodes[1].count;

            // Mark as a non character node
            combined.isCharNode = false;

            // Remove old nodes from the current list
            currentNodes.remove(nodes[0]);
            currentNodes.remove(nodes[1]);

            // Add new node to the list
            currentNodes.add(combined);

        }

        // Create new encoding hash map
        characterEncoding = new HashMap<>();

        // Mark our current root node as the only node left
        rootNode = currentNodes.get(0);

        // Add all the nodes too the hash map with the character encodings, for the
        // output later
        printNodes(currentNodes.get(0), "");
    }

    /**
     * This is used for adding nodes encoding too the dictionary
     * 
     * @param currentNode
     * @param string
     */
    public void printNodes(Node currentNode, String string) {

        // If the node is a character we reached the end and we add this encoding to our
        // dictionary of character encodings
        if (currentNode.isCharNode) {
            characterEncoding.put(currentNode.character, string);
        } else {
            // If not a character we add a 0 to the left node and 1 to the right nodes
            // strings and continue down that path until we find all the character nodes and
            // hence build their encoding on the way there
            printNodes(currentNode.leftNode, string + "0");
            printNodes(currentNode.rightNode, string + "1");
        }
    }

    /**
     * Used to compute the lowest 2 nodes in any given list of nodes Making sure the
     * list has more elements or an error will be thrown
     * 
     * @param nodesInput A list of the nodes to be sorted
     * @return The 2 lowest node in the given node list (Note this isn't sorted so
     *         possible sometimes that Node 0 is bigger than Node 1)
     */
    public Node[] getLowestNodes(List<Node> nodesInput) {

        // Create an array of size 2 for the output nodes
        Node[] output = new Node[2];

        for (Node n : nodesInput) {
            if (output[0] == null) {
                // If no node then we add the current node
                output[0] = n;
            } else if (output[1] == null) {
                // If no node then we add the current node
                output[1] = n;
            } else if (output[0].count > n.count) {
                // If our nodes value is lower than the given one we will replace the lowest
                // node with our own
                output[0] = n;
            } else if (output[1].count > n.count) {
                // Check for the same condition of the second lowest node
                output[1] = n;
            }
        }

        // Returns two lowest node
        return output;
    }

    /**
     * Used for testing the current map
     * 
     * @deprecated No longer used as out of testing phase, will be removed in next
     *             update kept in case of testing required
     */
    @Deprecated(since = "0.1", forRemoval = true)
    public void printMap() {
        // Only used in prelimenary testing to check the current positions of nodes
        currentPositions.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
    }

    /**
     * Takes the current encoding created when this object is created and writes it
     * too a file of a specificed file name
     * 
     * @param outputFileName The name of the file to output too
     * @throws IOException
     */
    public void outputEncoding(String outputFileName) throws IOException {

        // Create a new string builder (The solution to all problems)

        StringBuilder bld = new StringBuilder();
        // Iterate through the string and create a binary text string to output
        for (int i = 0; i < inputString.length(); i++) {
            bld.append(characterEncoding.get(inputString.charAt(i)));
        }

        String currentMsg = bld.toString();

        // Turn the binary string into a byte array
        byte[] data = decodeBinary(currentMsg);

        // Create a header holding all the encodings and the byte buffer
        byte[] header = encodingHeader();

        // Wrtie the header and then the data to the given file
        try (FileOutputStream fileStream = new FileOutputStream(outputFileName)) {
            fileStream.write(header);
            fileStream.write(data);
        }

    }

    /**
     * This will take a string of 1's and 0's and give a properly formatted byte
     * array even if the string isn't put into 8 bit bytes
     * 
     * @param s Binary String
     * @return Byte array representation of the binary string
     */
    public byte[] decodeBinary(String s) {

        // Check the string is a multiple of 8, hence can be made perfectly into bytes
        if (s.length() % 8 != 0) {
            // Find the next multiple of 8 up from our current value
            double highestValue = Math.ceil(s.length() / 8) + 1;

            // We then work out the difference between our value and the highest value, this
            // is the overflow byte integer
            overflowByte = ((int) highestValue * 8) - s.length();

            // Then add a series of zeros to the end of our binary string to fill this gap
            for (int i = overflowByte; i > 0; i--) {
                s += "0";
            }
        }

        // Create a new byte array and eigth of the size of the current string
        byte[] data = new byte[s.length() / 8];

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                // Place a one as a bit into the string filling the rest with zeros
                data[i >> 3] |= 0x80 >> (i & 0x7);
            } else if (c != '0') {
                throw new IllegalArgumentException("Character is not valid");
            }
        }

        // Return this created byte array
        return data;
    }

    /**
     * We use this to create a header for the encoding where we store two flags as
     * the string END, the current encodings and the overflow byte in string form.
     * 
     * @return A formatted header for the encoding as a byte array
     */
    public byte[] encodingHeader() {
        // Here we need to create a binary string of Character, Key
        Iterator currentCodes = characterEncoding.entrySet().iterator();

        StringBuilder bld = new StringBuilder();
        // Iterate the current character encoding and map it too a string
        while (currentCodes.hasNext()) {
            Map.Entry pair = (Map.Entry) currentCodes.next();
            // Using two character notation along with spaces so we can decode it later on
            bld.append(pair.getKey() + " --" + pair.getValue() + " Nx ");
        }
        String output = bld.toString();
        // Add two flags to allow us to seperate the string when encoding later
        output += " END " + overflowByte + " END ";

        // Return as a byte array
        return output.getBytes();
    }

    /**
     * Will decode as long as you can provide the encoded message and root node
     * 
     * @param encodedMessage
     * @param rootNode
     * @return
     * @deprecated Since we have a seperate decoder now this is no longer needed,
     *             kept for testing
     */
    @Deprecated(since = "0.2")
    public String decodeMessage(String encodedMessage, Node rootNode) {

        String currentMessage = "";

        // Generate a key list for each of the nodes
        Map<String, Character> currentEncoding = new HashMap<>();
        currentEncoding = createCurrentMap(rootNode, currentEncoding, "");

        // Create a blank string for each encoding
        String currentCheck = "";
        // Iterate through the string and check for it's exsistance in the key list
        for (int i = 0; i < encodedMessage.length(); i++) {
            char localChar = encodedMessage.charAt(i);
            currentCheck += localChar;

            if (currentEncoding.containsKey(currentCheck)) {
                currentMessage += currentEncoding.get(currentCheck);
                currentCheck = "";
            }

        }

        return currentMessage;

    }

    /**
     * Don't call until the encoding is created as otherwise will return null or an
     * empty node
     * 
     * @return The current root node
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Used locally from a root node to create a hash map of all the current nodes
     * 
     * @param currentNode
     * @param currentMap
     * @param currentEncoding
     * @return
     * @deprecated As we now have a decoder method this is no longer needed, kept
     *             for testing
     */
    @Deprecated(since = "0.2")
    public Map<String, Character> createCurrentMap(Node currentNode, Map<String, Character> currentMap,
            String currentEncoding) {
        if (currentNode.isCharNode) {
            currentMap.put(currentEncoding, currentNode.character);
        }

        else {
            createCurrentMap(currentNode.leftNode, currentMap, currentEncoding + "0");
            createCurrentMap(currentNode.rightNode, currentMap, currentEncoding + "1");
        }

        return currentMap;
    }

}
