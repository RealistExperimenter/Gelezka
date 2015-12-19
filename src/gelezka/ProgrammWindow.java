package gelezka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ProgrammWindow extends JFrame {

    JTextArea console;
    RefreshGelezo myProgramm;
    JFrame temp;
    private Thread refresh;

    ProgrammWindow() {
        super("Gelezo Refresher v2.1");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(600, 600);

        Container panel = this.getContentPane();
        console = new JTextArea(20, 20);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setEditable(false);
        Font font = new Font("Verdana", Font.BOLD, 12);
        console.setFont(font);
        panel.add(new JScrollPane(console));
        setResizable(false);
        addTopMenu();
        addCloseOperation();


    }

    private void addCloseOperation(){
        addWindowListener(new WindowListener() {

            public void windowActivated(WindowEvent event) {

            }

            public void windowClosed(WindowEvent event) {
                stopProgramm();
            }

            public void windowClosing(WindowEvent event) {
                Object[] options = { "Да", "Нет!" };
                int n = JOptionPane
                        .showOptionDialog(event.getWindow(), "Выйти из программы?",
                                "Подтверждение", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    event.getWindow().setVisible(false);
                    stopProgramm();
                }
            }

            public void windowDeactivated(WindowEvent event) {

            }

            public void windowDeiconified(WindowEvent event) {

            }

            public void windowIconified(WindowEvent event) {

            }

            public void windowOpened(WindowEvent event) {

            }

        });
    }

    private void addTopMenu() {

        JMenuBar menuBar = new JMenuBar();
        JMenu aboutMenu = new JMenu("О программе");
        JMenuItem about = new JMenuItem("Информация о программе");
        about.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {}

            public void mouseEntered(MouseEvent event) {}

            public void mouseExited(MouseEvent event) {}

            public void mousePressed(MouseEvent event) {showInfo();}

            public void mouseReleased(MouseEvent event) {}
        });

        aboutMenu.add(about);

        menuBar.add(aboutMenu);
        this.setJMenuBar(menuBar);
   }

    public void showInfoMessage(String info){
        JOptionPane.showMessageDialog(this,
            "У объявления: "+info+" новое сообщение.\n " +
                    "Зайдите на сайт чтобы прочитать и ответить на него.",
            "Новое сообщение",
            JOptionPane.WARNING_MESSAGE);}

    public void showMessageAlert(){
        JOptionPane.showMessageDialog(this,
                "У Вас новое личное сообщение.\n" +
                        " Зайдите на сайт чтобы прочитать и ответить на него.",
                "Новое сообщение",
                JOptionPane.WARNING_MESSAGE);}

    private void showInfo(){
        temp = new JFrame("Информация о программе");
        temp.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        temp.setSize(300, 230);
        JTextArea console2 = new JTextArea(20, 20);
        console2.setLineWrap(true);
        console2.setWrapStyleWord(true);
        console2.setEditable(false);
        Font font = new Font("Verdana", Font.BOLD, 8);
        console2.setFont(font);
        temp.setResizable(false);
        temp.getContentPane().add(console2);
        console2.append("Gezelo Refresher v2.1 \n\n Приложение предназначено для автоматического обновления объявлений на сайте www.gelezo.com.ua.\n\n C предложениями, пожеланиями, а также по всем вопросам получения лицензионного ключа обращайтесь на gelezo.refresh@gmail.com.\n\n Для реализации использована библиотека SeleniumWD www.seleniumhq.org\n" +
                "\n\n Приложение поставляется как есть(As is) и создатель не несет ответственности за любые возможные технические и прогаммные неполадки связанные с работой приложения. Также создатель не несет ответственности за все действия пользователя и третьих лиц совершенные с помощью данного приложения.");
        temp.setVisible(true);

    }

    public void addSting(String text) {
        console.append(text + "\n");
    }


    public void runProgramm() {
       myProgramm=new RefreshGelezo(this);
        myProgramm.refresh();
      // refresh=new Thread(myProgramm);
      //  refresh.start();
          }

    private void stopProgramm(){
        if (temp != null) {
            temp.dispose();
        }
     myProgramm.closeDriverStopProgramm();
       // if(refresh != null) {
       //     if (!refresh.isInterrupted()) {
      //          refresh.interrupt();
      //      }
     //   }
    }
}