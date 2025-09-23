package emptyimplementations;

import processapi.DataStorageAPI;
import processapi.ReadRequest;
import processapi.ReadResponse;
import processapi.WriteRequest;
import processapi.WriteResponse;

/*
 An empty implementation of the DataStorageAPI 
 This class handles reading from and writing to a data source,
*/
public class ProcessAPIImpl implements DataStorageAPI {

	// Reads data from a source.
	@Override
	public ReadResponse readData(ReadRequest request) {
		return new ReadResponseImpl();
	}

	// Writes data to a destination.
	@Override
	public WriteResponse writeData(WriteRequest request) {
		// Return a default failure response as the logic is not implemented.
		return new WriteResponseImpl();
	}
}