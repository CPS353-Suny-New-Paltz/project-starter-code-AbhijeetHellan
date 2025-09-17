package processapi;

import java.util.Arrays;
import project.annotations.ProcessAPIPrototype;
import java.util.List;

public class DataStoragePrototype {
	@ProcessAPIPrototype
	public void prototype(DataStorageAPI storage) {

		// Create a request to read data from a source (Mock file path)
		ReadRequest readRequest = new ReadRequest("file:///data/input.csv");
		// Read data using the storage API
		ReadResponse readResponse = storage.readData(readRequest);

		// Check the response and process the data
		if (readResponse.isSuccess()) {
			List<Integer> numbers = readResponse.getIntegerStream().getNumbers();
			System.out.println("Read operation successful. Read " + numbers.size() + " integers.");

			// Create a request to write some results back(Mock processing)
			List<String> formattedResults = Arrays.asList("1:one", "21:twenty-one", "105:one hundred five");
			// Write request to write data to a destination file(
			WriteRequest writeRequest = new WriteRequest("file:///data/output.txt", formattedResults);
			// Write data using the storage API
			WriteResponse writeResponse = storage.writeData(writeRequest);
			// Check the write response
			if (writeResponse.isSuccess()) {
				System.out.println("Write operation successful.");
			} else {
				System.out.println("Write operation failed.");
			}
		} else {
			System.out.println("Read operation failed.");
		}
	}
}