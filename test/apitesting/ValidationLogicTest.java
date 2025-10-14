package apitesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import conceptualapi.ComputationResultCode;
import conceptualapi.ComputeResponse;
import emptyimplementations.ConceptualAPIImpl;
import emptyimplementations.NetworkAPIImpl;
import emptyimplementations.ProcessAPIImpl;
import networkapi.JobResponse;
import networkapi.JobStatus;
import processapi.ReadRequest;
import processapi.ReadResponse;
import processapi.WriteResponse;

public class ValidationLogicTest {
    
    private ConceptualAPIImpl conceptualAPI;
    private NetworkAPIImpl networkAPI;
    private ProcessAPIImpl processAPI;
    
    @BeforeEach
    public void setUp() {
        conceptualAPI = new ConceptualAPIImpl();
        networkAPI = new NetworkAPIImpl(null, null); // Using nulls for validation testing
        processAPI = new ProcessAPIImpl();
    }
    
    @Test
    public void testConceptualAPIValidation() {
        // Test null request
        ComputeResponse response = conceptualAPI.compute(null);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ComputationResultCode.INVALID_INPUT, response.getResultCode());
    }
    
    @Test
    public void testNetworkAPIValidation() {
        // Test null job submission
        JobResponse response = networkAPI.submitJob(null);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(JobStatus.FAILED, response.getStatus());
        assertTrue(response.getMessage().contains("cannot be null"));
        
        // Test null job ID for status check
        JobResponse statusResponse = networkAPI.getJobStatus(null);
        assertNotNull(statusResponse);
        assertFalse(statusResponse.isSuccess());
        assertTrue(statusResponse.getMessage().contains("cannot be null"));
        
        // Test empty job ID for cancellation
        JobResponse cancelResponse = networkAPI.cancelJob("");
        assertNotNull(cancelResponse);
        assertFalse(cancelResponse.isSuccess());
        assertTrue(cancelResponse.getMessage().contains("cannot be null or empty"));
    }
    
    @Test
    public void testProcessAPIValidation() {
        // Test null read request
        ReadResponse readResponse = processAPI.readData(null);
        assertNotNull(readResponse);
        assertFalse(readResponse.isSuccess());
        assertNull(readResponse.getIntegerStream());
        
        // Test empty source path
        ReadRequest emptyRequest = new ReadRequest("");
        ReadResponse emptyResponse = processAPI.readData(emptyRequest);
        assertNotNull(emptyResponse);
        assertFalse(emptyResponse.isSuccess());
        
        // Test null write request
        WriteResponse writeResponse = processAPI.writeData(null);
        assertNotNull(writeResponse);
        assertFalse(writeResponse.isSuccess());
    }
}
