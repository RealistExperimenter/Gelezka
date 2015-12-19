package gelezka;


public class Message implements Runnable{
    private ProgrammWindow window;
    private String message;

    public Message(ProgrammWindow window, String message) {
        this.window = window;
        this.message = message;
    }

    public Message(ProgrammWindow window) {
    this.window=window;

    }

    @Override
    public void run() {
        if (message == null) {
            window.showMessageAlert();
        }
        else window.showInfoMessage(message);
    }
}
