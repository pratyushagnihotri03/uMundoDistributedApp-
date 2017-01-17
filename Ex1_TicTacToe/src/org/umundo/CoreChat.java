package org.umundo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.umundo.core.Discovery;
import org.umundo.core.Discovery.DiscoveryType;
import org.umundo.core.Greeter;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;

/**
 * Make sure to set the correct path to umundo.jar in build.properties if you want to use ant!
 */

public class CoreChat{

	/**
	 * Send and receive simple chat messages. This sample uses meta fields from
	 * a message to send chat strings.
	 */
	
	public Discovery disc;
	public Node chatNode;
	public Subscriber fooSub;
	public Publisher fooPub;
	
	int clicks = 0;
	public String userName ="Player";
	public HashMap<String, String> participants = new HashMap<String, String>();
	public BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public CoreChat() {
		disc = new Discovery(DiscoveryType.MDNS);
		
		chatNode = new Node();
		fooSub = new Subscriber("coreChat");
		fooSub.setReceiver(new ChatReceiver());
		
		fooPub = new Publisher("coreChat");
		
		disc.add(chatNode);
	
		System.gc();
		int uNo = 1;
		
		userName = userName+uNo;
		System.out.println(userName);
		fooPub.setGreeter(new ChatGreeter(userName));
		chatNode.addPublisher(fooPub);
		chatNode.addSubscriber(fooSub);
		uNo++;
	}
	
	public void addGreeter(String userName) {
		fooPub.setGreeter(new ChatGreeter(userName));
	}
	
	public void run(String key, String value) {
		
			Message msg = new Message();
			msg.putMeta(key, value);
			fooPub.send(msg);
		
	}
	
	class ChatReceiver extends Receiver {
		String value = "";
		int positionX=0;
		int positionO=0;
		@Override
		public void receive(Message msg) {
			if (msg.getMeta().containsKey("participant")) {
				CoreChat.this.participants.put(msg.getMeta("subscriber"), msg.getMeta("participant"));
				System.out.println(msg.getMeta("participant") + " joined the chat");
				TicTacToe.getInstance().setWindowName(msg.getMeta("participant"));
			} 
			else if(msg.getMeta().containsKey("X")) {
				String message = msg.getMeta("X");
				TicTacToe.getInstance().showDialog(message);
			}
			
			else if(msg.getMeta().containsKey("O")) {
				String message = msg.getMeta("O");
				TicTacToe.getInstance().showDialog(message);
			}
			
			else if(msg.getMeta().containsKey("tie")) {
				String message = msg.getMeta("tie");
				TicTacToe.getInstance().showDialog(message);
			}
			else if(msg.getMeta().containsKey("turn")) {
				int turn = Integer.parseInt(msg.getMeta("turn"));
				TicTacToe.getInstance().setTurn(turn);
			}
			
			else {
			
				String message1 = msg.getMeta("position");
				String message2 = msg.getMeta("value");
				System.out.print("Received :");				
				if (message2.contains("X")) {
					value = "X";
					System.out.print("X");
				}
				else if(message2.contains("O")) {
					value = "O";
					System.out.print("O");
				}
				if (!message1.isEmpty()) {
					if (value == "X" ) 
						positionX = Integer.parseInt(message1);
					else if(value == "O")
						positionO = Integer.parseInt(message1);
					System.out.print(positionO+positionX);
				}
				clicks++;
				
				//check if X is played and sends update for position and value
				if (value == "X" && positionX != 0) {
					System.out.println(value+ "," + positionX);
					TicTacToe.getInstance().setX(positionX);
					value="";
					positionX=0;
				}
				
				//check if O is played and sends update for position and value
				else if(value == "O" && positionO != 0) {
					System.out.println(value+ "," + positionO);
					TicTacToe.getInstance().setO(positionO);
					value="";
					positionO=0;
				}
			}
			
		}
	}
	
	class ChatGreeter extends Greeter {
		public String userName;

		public ChatGreeter(String userName) {
			this.userName = userName;
		}

		@Override
		public void welcome(Publisher pub, SubscriberStub subStub) {
			Message greeting = Message.toSubscriber(subStub.getUUID());
			greeting.putMeta("participant", userName);
			greeting.putMeta("subscriber", CoreChat.this.fooSub.getUUID());
			pub.send(greeting);
		}

		@Override
		public void farewell(Publisher pub, SubscriberStub subStub) {
			if (CoreChat.this.participants.containsKey(subStub.getUUID())) {
				System.out.println(CoreChat.this.participants.get(subStub.getUUID()) + " left the chat");
			} else {
				System.out.println("An unknown user left the chat: " + subStub.getUUID());	
			}
		}	
	}

}

