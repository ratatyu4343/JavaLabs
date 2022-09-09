import java.lang.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

public class Main {
    private static boolean flag = false;
    public static void main(String[] args)
    {
        JFrame win = new JFrame();
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.setSize(400,200);
        JPanel panel = new JPanel();
        JSlider slider = new JSlider(0,100,50);
        JButton btn = new JButton("Пуск");
        Thread t1 = new Thread(
                () -> {
                    while (true) {
                        synchronized (slider) {
                            if (slider.getValue() != 10) {
                                slider.setValue(slider.getValue() - 1);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException ee) {
                                }
                            }
                        }
                    }
                });
        Thread t2 = new Thread(
                () -> {
                    while (true) {
                        synchronized (slider) {
                            if (slider.getValue() != 90) {
                                slider.setValue(slider.getValue() + 1);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException ee) {
                                }
                            }
                        }
                    }
                });
        btn.addActionListener(
                (ActionEvent e) ->
                    {
                        if(!flag) {
                            t1.start();
                            t2.start();
                            flag = true;
                        }
                    }
                );
        SpinnerModel model1 = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner spinner1 = new JSpinner(model1);
        SpinnerModel model2 = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner spinner2 = new JSpinner(model2);
        spinner1.addChangeListener(
                (ChangeEvent c) -> {
                    t1.setPriority(Integer.parseInt(model1.getValue().toString()));
                });
        spinner2.addChangeListener(
                (ChangeEvent c) -> {
                    t2.setPriority(Integer.parseInt(model2.getValue().toString()));
                });
        panel.add(btn);
        panel.add(slider);
        panel.add(spinner1);
        panel.add(spinner2);
        win.setContentPane(panel);
        win.setVisible(true);
    }
}