package emptyimplementations;

import processapi.IntegerStream;
import processapi.ReadResponse;

public class ReadResponseImpl implements ReadResponse {

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public IntegerStream getIntegerStream() {
		return null;
	}

}
