package grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import conceptualapi.ComputationAPI;
import emptyimplementations.ConceptualAPIImpl;
import emptyimplementations.MultiThreadedNetworkAPIImpl;
import grpc.computeengine.ComputeEngineServiceGrpc;
import grpc.computeengine.JobCancelRequest;
import grpc.computeengine.JobRequest;
import grpc.computeengine.JobResponse;
import grpc.computeengine.JobStatusRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import networkapi.ComputeEngine;
import networkapi.Delimiters;
import networkapi.JobSubmission;

public class ComputeEngineServer {
    private Server server;
    private DataStoreGrpcClient dataStoreClient;

    public void start(int port, String dataStoreHost, int dataStorePort) throws IOException {
        // Connect to Data Store Server via gRPC
        dataStoreClient = new DataStoreGrpcClient(dataStoreHost, dataStorePort);
        
        server = ServerBuilder.forPort(port)
                .addService(new ComputeEngineServiceImpl(dataStoreClient))
                .build()
                .start();
        
        System.out.println(" Compute Engine Server started on port " + port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** Shutting down Compute Engine Server...");
            ComputeEngineServer.this.stop();
            System.err.println("*** Server shut down successfully");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
            try {
                if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                    server.shutdownNow();
                }
            } catch (InterruptedException e) {
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (dataStoreClient != null) {
            try {
                dataStoreClient.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9090;
        String dataStoreHost = args.length > 1 ? args[1] : "localhost";
        int dataStorePort = args.length > 2 ? Integer.parseInt(args[2]) : 9091;
        
        ComputeEngineServer server = new ComputeEngineServer();
        server.start(port, dataStoreHost, dataStorePort);
        server.blockUntilShutdown();
    }

    static class ComputeEngineServiceImpl extends ComputeEngineServiceGrpc.ComputeEngineServiceImplBase {
        private final ComputeEngine computeEngine;

        public ComputeEngineServiceImpl(DataStoreGrpcClient dataStore) {
            ComputationAPI computation = new ConceptualAPIImpl();
            this.computeEngine = new MultiThreadedNetworkAPIImpl(dataStore, computation);
        }

        @Override
        public void submitJob(JobRequest request, StreamObserver<JobResponse> responseObserver) {
            System.out.println("Job Submitted");
            System.out.println("  Input: " + (request.hasInputSource() ? request.getInputSource() : "N/A"));
            System.out.println("  Output: " + (request.hasOutputSource() ? request.getOutputSource() : "N/A"));
            
            try {
                String pairDelim = request.hasPairDelimiter() ? request.getPairDelimiter() : ",";
                String kvDelim = request.hasKeyValueDelimiter() ? request.getKeyValueDelimiter() : ":";
                
                Delimiters delimiters = new Delimiters(
                    pairDelim.isEmpty() ? ',' : pairDelim.charAt(0),
                    kvDelim.isEmpty() ? ':' : kvDelim.charAt(0)
                );
                
                JobSubmission jobSubmission = new JobSubmission(
                    request.getInputSource(),
                    request.getOutputSource(),
                    delimiters
                );
                
                networkapi.JobResponse apiResponse = computeEngine.submitJob(jobSubmission);
                
                JobResponse.Builder responseBuilder = JobResponse.newBuilder()
                        .setJobId(apiResponse.getJobId())
                        .setSuccess(apiResponse.isSuccess());
                
                if (apiResponse.getMessage() != null) {
                    responseBuilder.setMessage(apiResponse.getMessage());
                }
                if (apiResponse.getStatus() != null) {
                    responseBuilder.setStatus(apiResponse.getStatus().toString());
                }
                
                JobResponse response = responseBuilder.build();
                
                System.out.println(" Job " + apiResponse.getJobId() + " - " + 
                                 (apiResponse.isSuccess() ? "Success" : "Failed"));
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                System.err.println(" Error: " + e.getMessage());
                e.printStackTrace();
                
                JobResponse response = JobResponse.newBuilder()
                        .setJobId("error")
                        .setSuccess(false)
                        .setMessage("Error: " + e.getMessage())
                        .setStatus("FAILED")
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }

        @Override
        public void getJobStatus(JobStatusRequest request, StreamObserver<JobResponse> responseObserver) {
            networkapi.JobResponse apiResponse = computeEngine.getJobStatus(request.getJobId());
            
            JobResponse.Builder builder = JobResponse.newBuilder()
                    .setJobId(apiResponse.getJobId())
                    .setSuccess(apiResponse.isSuccess());
            
            if (apiResponse.getMessage() != null) builder.setMessage(apiResponse.getMessage());
            if (apiResponse.getStatus() != null) builder.setStatus(apiResponse.getStatus().toString());
            
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void cancelJob(JobCancelRequest request, StreamObserver<JobResponse> responseObserver) {
            networkapi.JobResponse apiResponse = computeEngine.cancelJob(request.getJobId());
            
            JobResponse.Builder builder = JobResponse.newBuilder()
                    .setJobId(apiResponse.getJobId())
                    .setSuccess(apiResponse.isSuccess());
            
            if (apiResponse.getMessage() != null) builder.setMessage(apiResponse.getMessage());
            if (apiResponse.getStatus() != null) builder.setStatus(apiResponse.getStatus().toString());
            
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        }
    }
}
