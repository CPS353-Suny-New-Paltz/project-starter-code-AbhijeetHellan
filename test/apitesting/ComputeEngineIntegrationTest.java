package apitesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import conceptualapi.ComputationAPI;
import emptyimplementations.ConceptualAPIImpl;
import emptyimplementations.NetworkAPIImpl;
import processapi.DataStorageAPI;
import infrastructure.InMemoryInputConfig;
import infrastructure.InMemoryOutputConfig;
import networkapi.ComputeEngine;
import networkapi.JobSubmission;
import infrastructure.InMemoryDataStorageAPI;

import java.util.Arrays;
import java.util.List;

class ComputeEngineIntegrationTest {

	@Test
	void submitJob() {

		// Define the input data and create your new config objects.
		List<Integer> inputData = Arrays.asList(1, 10, 25);
		InMemoryInputConfig inputConfig = new InMemoryInputConfig(inputData);
		InMemoryOutputConfig outputConfig = new InMemoryOutputConfig();

		//  Create the test-only data store using your config objects.
		DataStorageAPI dataStore = new InMemoryDataStorageAPI(inputConfig, outputConfig);

		// Create the real but empty implementations of the other components.
		ComputationAPI computation = new ConceptualAPIImpl();
		ComputeEngine computeEngine = new NetworkAPIImpl(dataStore, computation);

		// Submit a job to the engine.
		JobSubmission request = new JobSubmission("in-memory-input", "in-memory-output");
		computeEngine.submitJob(request);

		// Define the expected final output.
		List<String> expectedOutput = Arrays.asList("1:one", "10:ten", "25:twenty-five");

		// Check if the output config's list contains the expected results.
		// This will FAIL because the empty NetworkAPIImpl doesn't do anything.
		assertEquals(expectedOutput, outputConfig.getOutputData());
	}
}