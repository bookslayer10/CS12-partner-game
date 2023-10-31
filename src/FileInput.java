import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileInput {

	public static String[] getFileContents(String fileName) {
		String[] content = null;
		try {
			// Read the lines from the file and collect them into a list
			List<String> lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList());

			// copy the lines from the list into a 1D array
			content = lines.toArray(new String[0]);

		} catch (IOException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		} // try-catch

		return content;

	} // getFileContents

} // FileInput