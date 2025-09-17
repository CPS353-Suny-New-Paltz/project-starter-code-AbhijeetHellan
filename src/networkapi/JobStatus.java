package networkapi;

public enum JobStatus {
    PENDING(false),
    RUNNING(false),
    COMPLETED(true),
    FAILED(true),
    CANCELLED(true);

    public boolean check;
    JobStatus(boolean check) {
        this.check = check;
    }
    public boolean checkl() {
        return check;
    }
}