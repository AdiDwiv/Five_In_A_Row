package gui;

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.*;

import javax.swing.*;

import org.eclipse.jdt.annotation.NonNull;

import clui.BoardPrinter;
import clui.ConsoleController;
import controller.Controller;
import controller.DumbAI;
import controller.RandomAI;
import controller.SmartAI;
import model.Board;
import model.Game;
import model.GameListener;
import model.Line;
import model.Location;
import model.Player;
import model.Board.State;

public class MainV2 extends JFrame {
	/*
	 * Store choices of players
	 */
	JComboBox<String> playerChoice1;
	JComboBox<String> playerChoice2;
	
	/*
	 * Panel containing game grid
	 */
	JPanel panelTwo;
	JLabel gameStatePrinter;

	/*
	 * Game object
	 */
	Game game;
	

	/*
	 * List of human players
	 */
	ArrayList<Player> humanPlayers = new ArrayList<Player>();

	/*
	 * Creates single square panel for game grid
	 */
	public class Square extends JPanel implements GameListener {
		private boolean appearance;
		Player player;
		int row, col;
		//boolean inWinLine;

		/*
		 * Constructor that creates individual square of size 50x50
		 */
		public Square(int i, int j) {
			/*
			 * Shows whether a human user could play in a square
			 */
			appearance = false;
			//inWinLine = false;
			player = null;
			row = i;
			col= j;
			setPreferredSize(new Dimension(50, 50));

			this.addMouseListener(new MouseAdapter() {
				
				public void mouseClicked(MouseEvent e) {
					if( player == null) {
					if(humanPlayers.size() != 0) {
						if(humanPlayers.contains(game.nextTurn())) {
							game.submitMove(game.nextTurn(), new Location(row, col));
							}
						}
					}
				}

				public void mouseEntered(MouseEvent e) {
					if (player == null) {
						if(humanPlayers.size() != 0) {
							if(humanPlayers.contains(game.nextTurn())) {
						appearance = true;
						repaint();
							}
						}
					}
				}

				public void mouseExited(MouseEvent e) {
					appearance = false;
					repaint();
				}
			});
		}

		/*
		 * Custom paint function
		 */
		public @Override
		void paint(Graphics g) {
			if (!appearance) {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
			}

			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth(), getHeight());

			if (player == Player.X) {
				//if(!inWinLine)
				g.setColor(Color.BLUE);
				g.drawLine(2, 2, getWidth() - 5, getHeight() - 5);
				g.drawLine(2, getHeight() - 5, getWidth() - 5, 2);
			}
			if (player == Player.O) {
				//if(!inWinLine)
				g.setColor(Color.RED);
				g.drawArc(2, 2, getWidth() - 5, getHeight() - 5, 0, 360);
			}
		}

		/*
		 * Repaints square
		 */
		@Override
		public void gameChanged(Game g) {
			Board b = game.getBoard();
			player = b.get(row, col);
			
			paintImmediately(0, 0, getWidth(), getHeight());
			
			
			switch (g.getBoard().getState()) {
			case HAS_WINNER:
				setPanelThree(b.getWinner().winner + " wins!");
				break;
			case DRAW:
				setPanelThree("Game ended in a draw!");
				break;
			case NOT_OVER:
				if(gameStatePrinter != null) {
					gameStatePrinter.setText(game.nextTurn()+"'s turn");
					gameStatePrinter.paintImmediately(0, 0, getWidth(), getHeight());
				}
				break;
			}
			
		}

	}

	/*
	 * Constructor that sets up first panel
	 */
	public MainV2() {
		super("Five in a row v2");
		setLocation(350, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new Game();

		setPanelOne();
	}

	/*
	 * Build the first panel and add it to the JFrame
	 */
	public void setPanelOne() {
		final JPanel panelOne = new JPanel();
		JLabel label = new JLabel("Choose players");
		label.setPreferredSize(new Dimension(500, 30));
		label.setHorizontalAlignment(JLabel.CENTER);

		String[] playerChoiceList1 = { "X: Human", "X: DumbAI", "X: RandomAI",
				"X: SmartAI" };
		String[] playerChoiceList2 = { "O: DumbAI", "O: RandomAI",
				"O: SmartAI", "O: Human" };
		playerChoice1 = new JComboBox<String>(playerChoiceList1);
		playerChoice2 = new JComboBox<String>(playerChoiceList2);

		JButton playButton = new JButton("Play");
		playButton.setPreferredSize(new Dimension(300, 40));
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(panelOne);
				setPanelTwo();
				setupGame();
			}
		});

		panelOne.add(label, BorderLayout.NORTH);
		panelOne.add(playerChoice1, BorderLayout.WEST);
		panelOne.add(playerChoice2, BorderLayout.EAST);
		panelOne.add(playButton, BorderLayout.SOUTH);
		panelOne.setPreferredSize(new Dimension(500, 150));

		add(panelOne, BorderLayout.CENTER);
		pack();
	}

	/*
	 * Build the game grid and add it to JFrame
	 */
	public void setPanelTwo() {
		 panelTwo = new JPanel();
		
		panelTwo.setLayout(new GridLayout(Board.NUM_ROWS, Board.NUM_COLS));
		//board = new Square[Board.NUM_ROWS][Board.NUM_COLS];
		for (int i = 0; i < Board.NUM_ROWS; i++) {
			for (int j = 0; j < Board.NUM_COLS; j++) {
				Square s = new Square(i, j);
				panelTwo.add(s);
				//board[i][j] = s;
				game.addListener(s);
			}
		}
		gameStatePrinter = new JLabel(game.nextTurn()+"'s turn");
		gameStatePrinter.setPreferredSize(new Dimension(450, 30));
		
		add(panelTwo, BorderLayout.CENTER);
		add(gameStatePrinter, BorderLayout.SOUTH);
		
		revalidate();
		repaint();
		revalidate();
		repaint();
		pack();

	}
	
	/*
	 * Adds listener for players to game
	 */
	public void setupGame() {
		String playerXItem = (String) playerChoice1.getSelectedItem();
		String playerOItem = (String) playerChoice2.getSelectedItem();

		Controller playerX = null;
		Controller playerO = null;

		if (playerXItem.equals("X: Human")) {
			//playerX = new ConsoleController(Player.X);
			humanPlayers.add(Player.X);
		} 
		else if (playerXItem.equals("X: DumbAI")) {
			playerX = new DumbAI(Player.X);
			game.addListener(playerX);
		}
		else if (playerXItem.equals("X: RandomAI")) {
			playerX = new RandomAI(Player.X);
			game.addListener(playerX);
		}
		else {
			playerX = new SmartAI(Player.X);
			game.addListener(playerX);
		}

		if (playerOItem.equals("O: Human")) {
			//playerO = new ConsoleController(Player.O);
			humanPlayers.add(Player.O);
		}
		else if (playerXItem.equals("O: DumbAI")) {
			playerO = new DumbAI(Player.O);
			game.addListener(playerO);
		}
		else if (playerXItem.equals("O: RandomAI")) {
			playerO = new RandomAI(Player.O);
			game.addListener(playerO);
		}
		else {
			playerO = new SmartAI(Player.O);
			game.addListener(playerO);
		}
		
	}
	
	/*
	 * Builds the game-over panel
	 */
	public void setPanelThree(String result) {
		remove(panelTwo);
		remove(gameStatePrinter);
		final JPanel panelThree = new JPanel();
		panelThree.setPreferredSize(new Dimension(500, 500));
		JLabel declare = new JLabel();
		declare.setText(result);
		declare.setPreferredSize(new Dimension(500, 50));
		declare.setHorizontalAlignment(JLabel.CENTER);
		
		JButton playAgainButton = new JButton("Play Again");
		final MainV2 thisObject = this;
		playAgainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchAgain(thisObject);
			}
		});

		panelThree.add(declare, BorderLayout.CENTER);
		panelThree.add(playAgainButton, BorderLayout.SOUTH);
		add(panelThree, BorderLayout.CENTER);
		revalidate();
		repaint();
		pack();
	}
	
	private static void launchAgain(MainV2 obj) {
		new MainV2().setVisible(true);
		obj.dispose();
	}

	/*
	 * Launch JFrame
	 */
	public static void main(String[] args) {
		new MainV2().setVisible(true);
	}
}
