public class User {
    private String username;
    private ClientHandler handler;

    public User(String username, ClientHandler handler) {
        this.username = username;
        this.handler = handler;
    }

    public void sendMessage(String msg) {
        handler.sendMessage(msg);
    }
}
