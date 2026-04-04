package view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import dao.PlayerDAO;
import dao.entities.Player;

public class LoginPanel extends JPanel {
    private GameFrame frame;
    private PlayerDAO playerDAO;
    private JTextField userField;
    private JPasswordField passField;
    private Font gameFont;

    public LoginPanel(GameFrame frame) {
        this.frame = frame;
        this.playerDAO = new PlayerDAO();
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));
        loadFont();

        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBackground(new Color(45, 45, 45));
        loginBox.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("BRICK BREAKER LOGIN");
        title.setFont(gameFont.deriveFont(18f));
        title.setForeground(Color.CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        
        styleField(userField, "Username");
        styleField(passField, "Password");

        JButton loginBtn = createButton("LOGIN", Color.GREEN);
        JButton regBtn = createButton("REGISTER", Color.ORANGE);

        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> handleRegister());

        loginBox.add(title);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(userField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 10)));
        loginBox.add(passField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(loginBtn);
        loginBox.add(Box.createRigidArea(new Dimension(0, 10)));
        loginBox.add(regBtn);

        add(loginBox);
    }

    private void styleField(JTextField field, String title) {
        field.setMaximumSize(new Dimension(300, 40));
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, 0, 0, null, Color.LIGHT_GRAY));
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(300, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(gameFont.deriveFont(12f));
        return btn;
    }

    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        
        // Vô hiệu hóa nút để tránh bấm nhiều lần
        setButtonsEnabled(false);
        
        new SwingWorker<Player, Void>() {
            @Override
            protected Player doInBackground() throws Exception {
                return playerDAO.login(user, pass);
            }

            @Override
            protected void done() {
                try {
                    Player player = get();
                    if (player != null) {
                        frame.setCurrentPlayer(player);
                        frame.showMenu();
                    } else {
                        JOptionPane.showMessageDialog(LoginPanel.this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        setButtonsEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    setButtonsEnabled(true);
                }
            }
        }.execute();
    }

    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        if (user.length() < 3 || pass.length() < 3) {
            JOptionPane.showMessageDialog(this, "Tài khoản/Mật khẩu phải ít nhất 3 ký tự!");
            return;
        }
        
        setButtonsEnabled(false);
        
        new SwingWorker<Player, Void>() {
            @Override
            protected Player doInBackground() throws Exception {
                return playerDAO.register(user, pass, "");
            }

            @Override
            protected void done() {
                try {
                    Player player = get();
                    if (player != null) {
                        JOptionPane.showMessageDialog(LoginPanel.this, "Đăng ký thành công!");
                    } else {
                        JOptionPane.showMessageDialog(LoginPanel.this, "Tên tài khoản đã tồn tại hoặc lỗi server!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setButtonsEnabled(true);
            }
        }.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        // Tìm và set các nút trong loginBox
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                for (Component subC : ((JPanel) c).getComponents()) {
                    if (subC instanceof JButton) subC.setEnabled(enabled);
                }
            }
        }
    }

    private void loadFont() {
        try {
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) gameFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            else gameFont = new Font("Arial", Font.BOLD, 14);
        } catch (Exception e) { gameFont = new Font("Arial", Font.BOLD, 14); }
    }
}
