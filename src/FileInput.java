import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/* FileInput.java
 * Reads in required formatted files for use within the game
 */
public class FileInput {

	/* Reads in a formatted text file into an int[][] indicating the tile type with an integer
	 */
	public static int[][] getMapContents(String fileName) {
		int[][] content = new int[15][29];
		try {

			//Variable declarations:
			//----------------------
			InputStream input = FileInput.class.getClassLoader().getResourceAsStream(fileName);//load file in editor
			BufferedReader in = new BufferedReader(new InputStreamReader(input));//to read text within file
			
			in.mark(Short.MAX_VALUE);//see API

			for (int i = 0; i < content.length; i++) {
				String[] line = in.readLine().split("\s+");
				for (int j = 0; j < line.length; j++) {
					content[i][j] = Integer.parseInt(line[j]);
				} // for
			} // for
			in.close();

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
//			// Read the lines from the file and collect them into a list
//			String[] lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList()).toArray(new String[0]);
//			
			
			
			//Variable declarations:
			//----------------------
			String[] lines = new String[79];
			InputStream input = FileInput.class.getClassLoader().getResourceAsStream(fileName);//load file in editor
			BufferedReader in = new BufferedReader(new InputStreamReader(input));//to read text within file
			
			in.mark(Short.MAX_VALUE);//see API

			for (int i = 0; i < lines.length; i++) {
				lines[i] = in.readLine();
				if (lines[i] == null) {
					lines[i] = "";
				}
			} // for
			in.close();
			
			int i = 0; // which slide
			int j = 0; // which line within the slide
			for (String line: lines) {
				
				if (line == null) {
					line = "";
				}
				
				// switches to next slide upon reaching break indicator		
				if (line.equals("%%")) {
					i++;
					j = 0;
				} else {
					content[i][j++] = line;
					
				} // else			
			} // for

		} catch (IOException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		} // try-catch

		return content;

	} // getFileContents

} // FileInput