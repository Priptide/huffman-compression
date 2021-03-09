import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TxtToString {
    Path path;

    /**
     * Create a new text to string object getting the path of the text document
     * given the file name
     * 
     * @param fileName
     * @throws URISyntaxException
     */
    public TxtToString(String fileName) throws URISyntaxException {
        // Load the text document from it's filename
        path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
    }

    /**
     * Will extract the string from any given text document then format it with a
     * flag to later help in decoding
     * 
     * @return The string in the document with a flag
     * @throws IOException
     */
    public String getContents() throws IOException {
        // Get the string data of each line as stream
        Stream<String> currentLines = Files.lines(path);

        // Add all these streams together with new lines
        // along with a flag to the start of the string
        String outputData = " start " + currentLines.collect(Collectors.joining("\n"));

        // Close the stream to mitagate any memory leaks
        currentLines.close();

        // Return the string data
        return outputData;
    }
}