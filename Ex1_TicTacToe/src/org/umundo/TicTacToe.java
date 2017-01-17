package org.umundo;
/**
 * @author manishaluthra247
 * @description This is responsible to show tic tac toe game and sending update messages
 * via umundo
 * @state 
 * @changelog
 */
import javax.swing.*;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TicTacToe implements ActionListener {
	static CoreChat chat;
	static TicTacToe toe = null;
	final String VERSION = "1.0";
	
	JFrame window = new JFrame();
	
	JButton btnEmpty[] = new JButton[10];
	
	JPanel pnlNewGame = new JPanel(),
	pnlNorth = new JPanel(),
	pnlSouth = new JPanel();
	JLabel lblTitle = new JLabel("Tic-Tac-Toe");
	JPanel pnlTop = new JPanel();
	JPanel pnlPlayingField = new JPanel();
		
	final int winCombo[][] = new int[][] {
		{1, 2, 3}, {1, 4, 7}, {1, 5, 9},
		{4, 5, 6}, {2, 5, 8}, {3, 5, 7},
		{7, 8, 9}, {3, 6, 9}
		
	};
	final int X = 412, Y = 268, color = 190;
	boolean inGame = false;
	boolean win = false;
	boolean btnEmptyClicked = false;
	String message;
	int turn = 1;
	int wonNumber1 = 1, wonNumber2 = 1, wonNumber3 = 1;
	
	public TicTacToe() { 
		window.setTitle("Player1");
		window.setSize(X, Y);
		window.setLocation(450, 260);
		window.setResizable(false);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pnlNewGame.setLayout(new GridLayout(2, 1, 2, 10));
		pnlNorth.setLayout(new FlowLayout(FlowLayout.CENTER));
		pnlSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlNorth.setBackground(new Color(color-20, color-20, color-20));
		pnlSouth.setBackground(new Color(color, color, color));
		
		pnlTop.setBackground(new Color(color, color, color));
		pnlTop.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		pnlNewGame.setBackground(Color.blue);
		
		pnlPlayingField.setLayout(new GridLayout(3, 3, 2, 2));
		pnlPlayingField.setBackground(Color.black);
		for(int i=1; i<=9; i++) {
			btnEmpty[i] = new JButton();
			btnEmpty[i].setBackground(new Color(220, 220, 220));
			btnEmpty[i].addActionListener(this);
			pnlPlayingField.add(btnEmpty[i]);
		}
		
		pnlSouth.add(lblTitle);
		
		window.add(pnlNorth, BorderLayout.NORTH);
		window.add(pnlSouth, BorderLayout.CENTER);
		pnlSouth.setLayout(new GridLayout(2, 1, 2, 5));
		window.setVisible(true);
		showGame();
	}
	
	public static TicTacToe getInstance(){
	    if(toe==null){
	       toe = new TicTacToe();
	      }
	      return toe;
	  }
	
	public void actionPerformed(ActionEvent click) {
		Object source = click.getSource();
		for(int i=1; i<=9; i++) {
			if(source == btnEmpty[i] && turn < 10) {
				btnEmptyClicked = true;
				System.out.println("turn:"+turn);
				if(!(turn % 2 == 0)) {
					setX(i);
					sendMessages("value", "X");
					sendMessages("position",Integer.toString(i));
					sendMessages("turn", Integer.toString(turn));
					window.setTitle("Player2");
					
				}
				else {
					setO(i);
					sendMessages("value", "O");
					sendMessages("position",Integer.toString(i));
					sendMessages("turn", Integer.toString(turn));
					window.setTitle("Player1");
				}
				turn++;
				
			}
			
		}
		if(btnEmptyClicked) {
			checkWin();
			btnEmptyClicked = false;
		}
		if(!inGame) {
			btnEmpty[wonNumber1].setBackground(new Color(220, 220, 220));
			btnEmpty[wonNumber2].setBackground(new Color(220, 220, 220));
			btnEmpty[wonNumber3].setBackground(new Color(220, 220, 220));
		//	turn = 1;
			for(int i=1; i<10; i++) {
				btnEmpty[i].setText("");
				btnEmpty[i].setEnabled(true);
			}
			win = false;
			showGame();
		}
		if(inGame)
			showGame();
		else {
			clearPanelSouth();
			pnlSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			pnlNorth.setVisible(true);
			pnlSouth.add(lblTitle);
		}
	
	pnlSouth.setVisible(false);
	pnlSouth.setVisible(true);
	
	}
	
	public void setO(int i) {
		// TODO Auto-generated method stub
		
		btnEmpty[i].setText("O");
		btnEmpty[i].setEnabled(false);
		pnlPlayingField.requestFocus();
		
	}

	public void setX(int i) {
		// TODO Auto-generated method stub
		
		btnEmpty[i].setText("X");
		btnEmpty[i].setEnabled(false);
		pnlPlayingField.requestFocus();
		
	}
	
	public void setTurn(int turnV) {
		turn++ ;
		//turn = turnV;
	}
	
	public void setWindowName(String userName) {
		window.setTitle(userName);
	}
	
	public void showDialog(String message) {
		JOptionPane.showMessageDialog(null, message);
		window.dispose();
	}
	
	public void showGame() { 
		
		clearPanelSouth();
		inGame = true;
		pnlSouth.setLayout(new BorderLayout());
		pnlSouth.add(pnlPlayingField, BorderLayout.CENTER);
		pnlPlayingField.requestFocus();
		
	}
		
	public void checkWin() { 
		for(int i=0; i<7; i++) {
			if(
				!btnEmpty[winCombo[i][0]].getText().equals("") &&
				btnEmpty[winCombo[i][0]].getText().equals(btnEmpty[winCombo[i][1]].getText()) &&
				btnEmpty[winCombo[i][1]].getText().equals(btnEmpty[winCombo[i][2]].getText())
				
			) {
				win = true;
				wonNumber1 = winCombo[i][0];
				wonNumber2 = winCombo[i][1];
				wonNumber3 = winCombo[i][2];
				break;
			}
		}
		if(win || (!win && turn>9)) {
			if(win) {
				if(turn % 2 == 0) {
					message = "X has won!";
					sendMessages("X", message);
				}
				else {
					message = "O has won!";
					sendMessages("O", message);
				}
				win = false;
			} else if(!win && turn>9) {
				message = "No One Win , GAME OVER !!!!! ";
				sendMessages("tie", message);
				window.dispose();
				
			}
			showDialog(message);
			for(int i=1; i<=9; i++) {
				btnEmpty[i].setEnabled(false);
			}
		}

	
	}
	
	
	public void clearPanelSouth() {
		pnlSouth.remove(lblTitle);
		pnlSouth.remove(pnlTop);
		pnlSouth.remove(pnlPlayingField);
		pnlTop.remove(pnlNewGame);
		
	}
	
	public void addGreeter(String userName) {
		chat.addGreeter(userName);
	}
	
	
	public void sendMessages(String key, String value) {
		
		chat.run(key,value);
	}
	
	public static void main(String[] args) {
		chat = new CoreChat();
		toe = getInstance();
	}
	

}