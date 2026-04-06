package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import dao.ScoreDAO;
import dao.entities.Score;

public class LeaderboardPanel extends JPanel {
    private GameFrame frame;
    private ScoreDAO scoreDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton backBtn;

    public LeaderboardPanel(GameFrame frame) {
        this.frame = frame;
        this.scoreDAO = new ScoreDAO();
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        Font titleFont = loadFont(30f);
        Font tableFont = loadFont(12f);
        Font buttonFont = loadFont(15f);

        // ===== TITLE =====
        JLabel title = new JLabel("LEADERBOARD");
        title.setFont(titleFont);
        title.setForeground(Color.YELLOW);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // TABLE
        String[] columnNames = {"RANK", "PLAYER", "TOTAL SCORE"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(tableFont);
        table.setForeground(Color.WHITE);
        table.setBackground(Color.BLACK);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // TAO TIEU DE BANG
        table.getTableHeader().setBackground(new Color(50, 50, 50));
        table.getTableHeader().setForeground(Color.YELLOW);
        table.getTableHeader().setFont(tableFont);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Can giua noi dung cac o
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(Color.BLACK);
        centerRenderer.setForeground(Color.WHITE);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        scrollPane.getViewport().setBackground(Color.BLACK);
        add(scrollPane, BorderLayout.CENTER);

        // BACK BUTTON 
        backBtn = new JButton("BACK TO MENU");
        backBtn.setFont(buttonFont);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(Color.DARK_GRAY);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> this.frame.showMenu());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshLeaderboard() {
        tableModel.setRowCount(0);
        new SwingWorker<List<Score>, Void>() {
            @Override
            protected List<Score> doInBackground() throws Exception {
                return scoreDAO.getLeaderboardByMaxScoreSum();
            }

            @Override
            protected void done() {
                try {
                    List<Score> scores = get();
                    if (scores == null || scores.isEmpty()) {
                        tableModel.addRow(new Object[]{"-", "NO DATA RECORDED", "-"});
                    } else {
                        int rank = 1;
                        for (Score s : scores) {
                            tableModel.addRow(new Object[]{
                                rank++,
                                s.getUserName(),
                                String.format("%,d", s.getScore())
                            });
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error displaying leaderboard: " + e.getMessage());
                    e.printStackTrace();
                    tableModel.addRow(new Object[]{"ERR", "ERROR: " + e.getMessage(), "ERR"});
                }
            }
        }.execute();
    }

    private Font loadFont(float size) {
        try {
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) {
                return Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
            }
        } catch (Exception e) {}
        return new Font("Arial", Font.BOLD, (int) size);
    }
}
