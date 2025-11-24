package grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import grpc.computeengine.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UserClient {
    private final ManagedChannel channel;
    private final ComputeEngineServiceGrpc.ComputeEngineServiceBlockingStub stub;

    public UserClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = ComputeEngineServiceGrpc.newBlockingStub(channel);
        System.out.println("Connected to Compute Engine at " + host + ":" + port + "\n");
    }

    public void submitJob(String input, String output, String pairDelim, String kvDelim) {
        System.out.println("Submitting job...\n");
        
        JobRequest.Builder requestBuilder = JobRequest.newBuilder()
                .setInputSource(input)
                .setOutputSource(output);
        
        if (pairDelim != null && !pairDelim.isEmpty()) {
            requestBuilder.setPairDelimiter(pairDelim);
        }
        if (kvDelim != null && !kvDelim.isEmpty()) {
            requestBuilder.setKeyValueDelimiter(kvDelim);
        }
        
        JobRequest request = requestBuilder.build();

        try {
            JobResponse response = stub.submitJob(request);
            
            System.out.println("========================================");
            System.out.println("           JOB RESULT");
            System.out.println("========================================");
            System.out.println("Job ID:  " + response.getJobId());
            System.out.println("Status:  " + (response.hasStatus() ? response.getStatus() : "N/A"));
            System.out.println("Success: " + (response.getSuccess() ? " YES" : "NO"));
            if (response.hasMessage()) {
                System.out.println("Message: " + response.getMessage());
            }
            System.out.println("========================================\n");
            
            if (response.getSuccess()) {
                showOutput(output);
            }
            
        } catch (Exception e) {
            System.err.println("Error submitting job: " + e.getMessage());
        }
    }

    private void showOutput(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                String content = Files.readString(file.toPath());
                System.out.println("Output File Content:");
                System.out.println("----------------------------------------");
                System.out.println(content);
                System.out.println("----------------------------------------\n");
            } else {
                System.out.println("(Output file not found)\n");
            }
        } catch (Exception e) {
            System.out.println("(Could not read output file: " + e.getMessage() + ")\n");
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9090;
        
        UserClient client = new UserClient(host, port);

        System.out.println("========================================");
        System.out.println("   NUMBER TO WORDS CONVERTER");
        System.out.println("========================================\n");
        
        // Input method
        System.out.println("How would you like to provide input?");
        System.out.println("  1. File path");
        System.out.println("  2. Type numbers manually");
        System.out.print("Choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        String inputPath;
        if (choice == 1) {
            System.out.print("Enter input file path: ");
            inputPath = scanner.nextLine();
        } else {
            System.out.print("Enter numbers (comma-separated, e.g., 1,5,10,25): ");
            String numbers = scanner.nextLine();
            
            File temp = File.createTempFile("input", ".txt");
            Files.write(temp.toPath(), numbers.getBytes());
            inputPath = temp.getAbsolutePath();
            temp.deleteOnExit();
            System.out.println("Created temp file: " + inputPath);
        }
        
        // Output file
        System.out.print("Enter output file path (e.g., output.txt): ");
        String outputPath = scanner.nextLine();
        
        // Delimiters (optional)
        System.out.print("Pair delimiter (press Enter for default ','): ");
        String pairDelim = scanner.nextLine();
        
        System.out.print("Key-value delimiter (press Enter for default ':'): ");
        String kvDelim = scanner.nextLine();
        
        System.out.println();
        client.submitJob(inputPath, outputPath, pairDelim, kvDelim);
        client.shutdown();
        scanner.close();
    }
}
