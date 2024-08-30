import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuVisualizer extends JPanel {

    static int N = 9;
    static int SIZE = 70; // Size of each cell
    static int[][] grid = new int[N][N];
    static int[][] initialGrid = new int[N][N]; // To store the initial state
    static boolean[][] editableCells = new boolean[N][N]; // Track editable cells
    static Random rand = new Random();
    static JLabel timerLabel;
    static JLabel scoreLabel;
    static long startTime;
    static Timer timer;
    static boolean timerStarted = false; // To track if the timer has started
    static int score = 0;

    // To keep track of the selected cell
    static int selectedRow = 0;
    static int selectedCol = 0;

    // Predefined Sudoku puzzles
    static List<int[][]> predefinedPuzzles = new ArrayList<>();

    public SudokuVisualizer() {
        setPreferredSize(new Dimension(N * SIZE, N * SIZE));
        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleArrowKeys(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyInput(e);
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the grid
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                g.setColor(Color.WHITE);
                g.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * SIZE, i * SIZE, SIZE, SIZE);

                // Highlight the selected cell
                if (i == selectedRow && j == selectedCol) {
                    g.setColor(new Color(0, 0, 255, 77)); // Blue color with 30% opacity
                    g.fillRect(j * SIZE, i * SIZE, SIZE, SIZE);
                }

                if (grid[i][j] != 0) {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.BOLD, 20));
                    g.drawString(Integer.toString(grid[i][j]), j * SIZE + SIZE / 3, i * SIZE + 2 * SIZE / 3);
                }
            }
        }
    }

    boolean solveSudoku(int row, int col) {
        if (row == N - 1 && col == N) {
            return true;
        }

        if (col == N) {
            row++;
            col = 0;
        }

        if (grid[row][col] != 0) {
            return solveSudoku(row, col + 1);
        }

        for (int num = 1; num <= 9; num++) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                repaint();
                try {
                    Thread.sleep(100); // Pause to visualize the step
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (solveSudoku(row, col + 1)) {
                    return true;
                }
            }
            grid[row][col] = 0;
            repaint();
            try {
                Thread.sleep(0); // Pause to visualize the backtrack
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    static boolean isSafe(int[][] grid, int row, int col, int num) {
        for (int x = 0; x < 9; x++)
            if (grid[row][x] == num)
                return false;

        for (int x = 0; x < 9; x++)
            if (grid[x][col] == num)
                return false;

        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[i + startRow][j + startCol] == num)
                    return false;

        return true;
    }

    static void generatePuzzleFromList(int[][] grid) {
        // Define predefined puzzles
        
        predefinedPuzzles.add(new int[][]{
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},
            {2, 1, 4, 3, 6, 5, 8, 9, 7},
            {3, 6, 5, 8, 9, 7, 2, 1, 4},
            {8, 9, 7, 2, 1, 4, 3, 6, 5},
            {5, 3, 1, 6, 4, 2, 9, 7, 8},
            {6, 4, 2, 9, 7, 8, 5, 3, 1},
            {9, 7, 8, 5, 3, 1, 6, 4, 2}
        });
        predefinedPuzzles.add(new int[][]{
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        });
         predefinedPuzzles.add(new int[][]{
            {4, 3, 5, 2, 6, 9, 7, 8, 1},
            {6, 8, 2, 5, 7, 1, 4, 9, 3},
            {1, 9, 7, 8, 3, 4, 5, 6, 2},
            {8, 2, 6, 1, 9, 5, 3, 4, 7},
            {3, 7, 4, 6, 8, 2, 9, 1, 5},
            {9, 5, 1, 7, 4, 3, 6, 2, 8},
            {5, 1, 9, 3, 2, 6, 8, 7, 4},
            {2, 4, 8, 9, 5, 7, 1, 3, 6},
            {7, 6, 3, 4, 1, 8, 2, 5, 9}
        });
        // Add more puzzles similarly...

        int[][] selectedPuzzle = predefinedPuzzles.get(rand.nextInt(predefinedPuzzles.size()));
        copyGrid(selectedPuzzle, grid);

        // Optionally remove some cells to make the puzzle more challenging
        int cellsToRemove = rand.nextInt(11) + 30; // Randomly remove between 30 and 40 cells
        while (cellsToRemove > 0) {
            int row = rand.nextInt(9);
            int col = rand.nextInt(9);
            if (grid[row][col] != 0) {
                grid[row][col] = 0;
                cellsToRemove--;
            }
        }

        // Copy the grid to initialGrid and mark the empty cells as editable
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                initialGrid[i][j] = grid[i][j];
                editableCells[i][j] = grid[i][j] == 0;
            }
        }
    }

    static void copyGrid(int[][] source, int[][] destination) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                destination[i][j] = source[i][j];
            }
        }
    }

    private void handleArrowKeys(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (selectedRow > 0) {
                    selectedRow--;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (selectedRow < N - 1) {
                    selectedRow++;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (selectedCol > 0) {
                    selectedCol--;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (selectedCol < N - 1) {
                    selectedCol++;
                }
                break;
        }
        repaint();
    }

    private void handleKeyInput(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (keyChar >= '1' && keyChar <= '9') {
            int num = keyChar - '0';
            if (editableCells[selectedRow][selectedCol] && isSafe(grid, selectedRow, selectedCol, num)) {
                grid[selectedRow][selectedCol] = num;
                repaint();
                score += 10; // Increase score on valid input
                updateScoreLabel();

                // Start the timer on the first valid input
                if (!timerStarted) {
                    startTimer();
                }
            }
        } else if (keyChar == KeyEvent.VK_BACK_SPACE || keyChar == KeyEvent.VK_DELETE) {
            // Allow users to clear the cell if they want to change their input
            if (editableCells[selectedRow][selectedCol]) {
                grid[selectedRow][selectedCol] = 0;
                repaint();
                score -= 5; // Decrease score on cell clear
                updateScoreLabel();
            }
        }
    }

    private void startTimer() {
        timerStarted = true;
        startTime = System.currentTimeMillis();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText("Time: " + elapsedTime + " s");
            }
        });
        timer.start();
    }

    private void resetGrid() {
        // Copy the initialGrid back to grid and reset editable cells
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = initialGrid[i][j];
                editableCells[i][j] = grid[i][j] == 0;
            }
        }
        selectedRow = 0;
        selectedCol = 0;
        timerStarted = false;
        timerLabel.setText("Time: 0 s");
        repaint();
    }

    private void giveHint() {
        // Find an empty cell and provide a hint (i.e., fill it with the correct number)
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(grid, i, j, num)) {
                            grid[i][j] = num;
                            repaint();
                            JOptionPane.showMessageDialog(this, "Hint: Row " + (i + 1) + ", Column " + (j + 1) + " should be " + num);
                            return;
                        }
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(this, "No hints available.");
    }

    private void solvePuzzle() {
        // Start solving the puzzle and update the UI accordingly
        new Thread(() -> {
            if (isSudokuSolved()) {
                JOptionPane.showMessageDialog(this, "Congratulations! You have solved the Sudoku manually.");
                return;
            }
    
            if (solveSudoku(0, 0)) {
                if (timer != null) {
                    timer.stop();
                }
                JOptionPane.showMessageDialog(this, "Sudoku Solved!");
            } else {
                if (timer != null) {
                    timer.stop();
                }
                JOptionPane.showMessageDialog(this, "No Solution exists", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
    
    private boolean isSudokuSolved() {
        // Check if all rows are valid
        for (int i = 0; i < N; i++) {
            if (!isValidUnit(grid[i])) {
                return false;
            }
        }
    
        // Check if all columns are valid
        for (int j = 0; j < N; j++) {
            int[] col = new int[N];
            for (int i = 0; i < N; i++) {
                col[i] = grid[i][j];
            }
            if (!isValidUnit(col)) {
                return false;
            }
        }
    
        // Check if all 3x3 subgrids are valid
        for (int r = 0; r < N; r += 3) {
            for (int c = 0; c < N; c += 3) {
                int[] subgrid = new int[N];
                int index = 0;
                for (int i = r; i < r + 3; i++) {
                    for (int j = c; j < c + 3; j++) {
                        subgrid[index++] = grid[i][j];
                    }
                }
                if (!isValidUnit(subgrid)) {
                    return false;
                }
            }
        }
    
        return true;
    }
    
    private boolean isValidUnit(int[] unit) {
        boolean[] seen = new boolean[N];
        for (int num : unit) {
            if (num < 1 || num > 9 || seen[num - 1]) {
                return false;
            }
            seen[num - 1] = true;
        }
        return true;
    }
    
    
    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    private void checkSolution() {
        if (isSudokuSolved()) {
            JOptionPane.showMessageDialog(this, "Congratulations! You have solved the Sudoku correctly.");
        } else {
            JOptionPane.showMessageDialog(this, "The Sudoku is not solved correctly.");
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Sudoku Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SudokuVisualizer sudokuPanel = new SudokuVisualizer();
        frame.add(sudokuPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        frame.add(controlPanel, BorderLayout.SOUTH);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> sudokuPanel.resetGrid());
        controlPanel.add(resetButton);

        JButton hintButton = new JButton("Hint");
        hintButton.addActionListener(e -> sudokuPanel.giveHint());
        controlPanel.add(hintButton);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(e -> sudokuPanel.solvePuzzle());
        controlPanel.add(solveButton);

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(e -> sudokuPanel.checkSolution());
        controlPanel.add(checkButton);

        scoreLabel = new JLabel("Score: 0");
        controlPanel.add(scoreLabel);

        timerLabel = new JLabel("Time: 0 s");
        controlPanel.add(timerLabel);

        frame.pack();
        frame.setVisible(true);

        generatePuzzleFromList(grid);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuVisualizer::createAndShowGUI);
    }
}

