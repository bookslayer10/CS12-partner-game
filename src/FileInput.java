import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileInput {

	public static int[][] getFileContents(String fileName) {
		int[][] content = new int[15][29];
		try {
			// Read the lines from the file and collect them into a list
			String[] lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList()).toArray(new String[0]);
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

	} // getFileContents

} // FileInput