package tiny_url.app.backend.common;

import java.time.LocalDate;

public class ShortenResponse {
    private int EC;
    private String MS;
    private String data = null;
    private String status = "OFF";
    private LocalDate createdAt = LocalDate.now();

    public ShortenResponse(int EC, String MS, String data, String status, LocalDate createdAt) {
        this.EC = EC;
        this.MS = MS;
        this.data = data;
        this.status = status;
        this.createdAt = createdAt;
    }

    public ShortenResponse(int EC, String MS, String data) {
        this.EC = EC;
        this.MS = MS;
        this.data = data;
    }

    public int getEC() {
        return EC;
    }

    public void setEC(int EC) {
        this.EC = EC;
    }

    public String getMS() {
        return MS;
    }

    public void setMS(String MS) {
        this.MS = MS;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
