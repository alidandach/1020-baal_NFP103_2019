public class Client {
    private String username;
    private String ip;
    private int port;

    public Client(String usr, String ip, int port){
        username=usr;
        this.ip=ip;
        this.port=port;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
