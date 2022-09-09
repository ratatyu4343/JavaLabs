import java.lang.*;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class Main {
    public static int semafor = 0;
    public static void main(String[] args)
    {
        JFrame win = new JFrame();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(400,200);
        JPanel panel = new JPanel();
        JSlider slider = new JSlider(0,100,50);
        JButton start1 = new JButton("Пуск1");
        JButton start2 = new JButton("Пуск2");
        JButton stop1 = new JButton("Стоп1");
        stop1.setEnabled(false);
        JButton stop2 = new JButton("Стоп2");
        stop2.setEnabled(false);
        JLabel label = new JLabel("Ресурс вільний");
        Thread t1 = new Thread(
                () -> {
                    while (true) {
                            if (slider.getValue() != 10) {
                                slider.setValue(slider.getValue() - 1);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException ee) {
                                }
                        }
                    }
                });
        Thread t2 = new Thread(
                () -> {
                    while (true) {
                            if (slider.getValue() != 90) {
                                slider.setValue(slider.getValue() + 1);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException ee) {
                                }
                        }
                    }
                });
        start1.addActionListener((ActionEvent e) -> {
            if(semafor == 0) {
                semafor = 1;
                stop1.setEnabled(true);
                stop2.setEnabled(false);
                t1.start();
                t1.setPriority(1);
                label.setText("Ресурс зайняти!");
            }
        });
        start2.addActionListener((ActionEvent e) -> {
            if(semafor == 0) {
                semafor = 1;
                stop2.setEnabled(true);
                stop1.setEnabled(false);
                t2.start();
                t2.setPriority(10);
                label.setText("Ресурс зайняти!");
            }
        });
        stop1.addActionListener((ActionEvent e) -> {
            semafor = 0;
            t1.stop();
            label.setText("Ресурс вільний!");
        });
        stop2.addActionListener((ActionEvent e) -> {
            semafor = 0;
            t2.stop();
            label.setText("Ресурс вільний!");
        });
        panel.add(start1);
        panel.add(start2);
        panel.add(stop1);
        panel.add(stop2);
        panel.add(slider);
        panel.add(label);
        win.setContentPane(panel);
        win.setVisible(true);
    }
}