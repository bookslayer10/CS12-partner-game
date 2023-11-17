import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/* FileInput.java
 * Reads in required formatted files for use within the game
 */
public class FileInput {

	/* Reads in a formatted text file into an int[][] indicating the tile type with an integer
	 */
	public static int[][] getMapContents(String fileName) {
		int[][] content = new int[15][29];
		try {
			// Read the lines from the file and collect them into a list
			URL url = FileInput.class.getResource(fileName);
			URI uri = null;
			try {
				uri = url.toURI();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] lines = Files.lines(Paths.get(uri)).collect(Collectors.toList()).toArray(new String[0]);
			// copy the lines from the list into a 1D array
			for (int i = 0; i < lines.length; i++) {
				String[] line = lines[i].split("\s+");
				for (int j = 0; j < line.length; j++) {
					content[i][j] = Integer.parseInt(line[j]);
				} // for
			} // for

		} catch (IOException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		} // try-catch

		return content;

	} // getMapContents
	
	/* Reads in a formatted text file with "%%" to indicate the breaks between separate slides of instructions
	 * returns content with each subarray an as instruction slide split into an array of lines
	 */
	public static String[][] getInstructions(String fileName) {
		String[][] content = new String[5][15];
		try {
			// Read the lines from the file and collect them into a list
			String[] lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList()).toArray(new String[0]);
			
			int i = 0; // which slide
			int j = 0; // which line within the slide
			for (String line: lines) {
				
				// switches to next slide upon reaching break indicator
				if (line.equals("%%")) {
					i++;
					j = 0;
				} else {
					content[i][j++] = line;
				} // else			
			}

		} catch (IOException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		} // try-catch

		return content;

	} // getFileContents

} // FileInput