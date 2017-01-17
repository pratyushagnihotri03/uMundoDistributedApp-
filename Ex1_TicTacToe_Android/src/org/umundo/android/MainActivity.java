package org.umundo.android;
import java.util.HashMap;

import org.umundo.core.Discovery;
import org.umundo.core.Greeter;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;
import org.umundo.core.Discovery.DiscoveryType;

import com.example.ex1_tictactoe_android.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author manishaluthra247, pratyushagnihotri
 * This is the main activity for game play
 *
 */

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	TextView tv;
	Discovery disc;
	Node chatNode;
	Publisher fooPub;
	Subscriber fooSub;
	Button btn;
	String message;
	public String userName="Player";
	public HashMap<String, String> participants = new HashMap<String, String>();

	/**
	 * @author manishaluthra247
	 * Receiver 
	 */
	public class ChatReceiver extends Receiver {
		String value;
		int positionX, positionO;
		public void receive(Message msg) {
			System.out.println("here22");
			for (String key : msg.getMeta().keySet()) {
				if (msg.getMeta().containsKey("participant")) {
					MainActivity.this.participants.put(msg.getMeta("subscriber"), msg.getMeta("participant"));
					System.out.println(msg.getMeta("participant") + " joined the chat");
					userName = msg.getMeta("participant");
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//playerLbl.setText(userName);
						}
					});
				} 
				
				if(msg.getMeta().containsKey("X")) {
					message = msg.getMeta("X");
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
						}
					});
					
				}
				
				else if(msg.getMeta().containsKey("O")) {
					message = msg.getMeta("O");
					
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				else if(msg.getMeta().containsKey("tie")) {
					message = msg.getMeta("tie");
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				else {
						System.out.println(msg.getMeta("position") + ": "
								+ msg.getMeta("value"));
						String message1 = msg.getMeta("position");
						String message2 = msg.getMeta("value");
						
						
						if (message2.contains("X")) {
							value = "X";
						}
						else if(message2.contains("O")) {
							value = "O";
						}
						if (!message1.isEmpty()) {
							if (value == "X" ) 
								positionX = Integer.parseInt(message1);
							else if(value == "O")
								positionO = Integer.parseInt(message1);
						}
											
						if (value == "X" && positionX != 0) {
							System.out.println(value+ "," + positionX);
							
							switch(positionX) {
								case 1: btn = btn00; break;
								case 2: btn = btn01; break;
								case 3: btn = btn02; break;
								case 4: btn = btn10; break;
								case 5: btn = btn11; break;
								case 6: btn = btn12; break;
								case 7: btn = btn20; break;
								case 8: btn = btn21; break;
								case 9: btn = btn22; break;
								default: break;
							}
							//TicTacToe.getInstance().setX(positionX);
							MainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
										//tv.setText(tv.getText() + "i");
									btn.performClick();
								}
							});
							value="";
							positionO=0;
						}
						else if(value == "O" && positionO != 0) {
							System.out.println(value+ "," + positionO);
							
							switch(positionO) {
							case 1: btn = btn00; break;
							case 2: btn = btn01; break;
							case 3: btn = btn02; break;
							case 4: btn = btn10; break;
							case 5: btn = btn11; break;
							case 6: btn = btn12; break;
							case 7: btn = btn20; break;
							case 8: btn = btn21; break;
							case 9: btn = btn22; break;
							default: break;
						}
						
						MainActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								btn.performClick();
							}
						});
						value="";
						positionO=0;
						}
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
			greeting.putMeta("subscriber", MainActivity.this.fooSub.getUUID());
			pub.send(greeting);
		}

		@Override
		public void farewell(Publisher pub, SubscriberStub subStub) {
			if (MainActivity.this.participants.containsKey(subStub.getUUID())) {
				System.out.println(MainActivity.this.participants.get(subStub.getUUID()) + " left the chat");
			} else {
				System.out.println("An unknown user left the chat: " + subStub.getUUID());	
			}
		}	
	}
	
    /** Called when the activity is first created. */
          Button btn00,btn01,btn02,btn10,btn11,btn12,btn20,btn21,btn22;
          AlertDialog.Builder alert,error;
          TextView playerLbl,header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_final);
    	WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			MulticastLock mcLock = wifi.createMulticastLock("mylock");
			mcLock.acquire();
			
		} else {
			Log.v("android-umundo", "Cannot get WifiManager");
		}

		System.loadLibrary("umundoNativeJava_d");

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
        setClickEvent();
    }

    public void setClickEvent()
    {
          btn00=(Button)findViewById(R.id.btn00);
          btn01=(Button)findViewById(R.id.btn01);
          btn02=(Button)findViewById(R.id.btn02);
          btn10=(Button)findViewById(R.id.btn10);
          btn11=(Button)findViewById(R.id.btn11);
          btn12=(Button)findViewById(R.id.btn12);
          btn20=(Button)findViewById(R.id.btn20);
          btn21=(Button)findViewById(R.id.btn21);
          btn22=(Button)findViewById(R.id.btn22);
          btn00.setOnClickListener(new Clicker());
          btn01.setOnClickListener(new Clicker());
          btn02.setOnClickListener(new Clicker());
          btn10.setOnClickListener(new Clicker());
          btn11.setOnClickListener(new Clicker());
          btn12.setOnClickListener(new Clicker());
          btn20.setOnClickListener(new Clicker());       
          btn21.setOnClickListener(new Clicker());
          btn22.setOnClickListener(new Clicker());
       }
    
    public void sendMessages(String key, String value) {
    	System.out.println(key+ ","+ value);
		Message msg = new Message();
		msg.putMeta(key, value);
		fooPub.send(msg);
	}

    public void resetButton()
      {
    			 btn00.setText("");
                 btn01.setText("");
                 btn02.setText("");
                 btn10.setText("");
                 btn11.setText("");
                 btn12.setText("");
                 btn20.setText("");
                 btn21.setText("");
                 btn22.setText("");
                playerLbl.setText("Player 1");
                 header=(TextView)findViewById(R.id.header);
    }

   
    public boolean determineWinner()
    {
          
          if(btn00.getText().toString().equals("X")&&
        		  btn01.getText().toString().equals("X") && btn02.getText().toString().equals("X"))
          {
        	     String message = "X has won";
        	     Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                 sendMessages("X", message);
        	  
                 return true;
          }

          else if(btn00.getText().toString().equals("X") && btn10.getText().toString().equals("X") && btn20.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
     	     sendMessages("X", message);
           
              return true;
          }

          else if(btn10.getText().toString().equals("X") && btn11.getText().toString().equals("X") && btn12.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
     	      sendMessages("X", message);
           
              return true;
          }

          else if(btn20.getText().toString().equals("X") && btn21.getText().toString().equals("X") && btn22.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
     	     sendMessages("X", message);
           
              return true;
          }

          else if(btn01.getText().toString().equals("X") && btn11.getText().toString().equals("X") && btn21.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
          
     	     sendMessages("X", message);
              return true;
          }

          else if(btn02.getText().toString().equals("X") && btn12.getText().toString().equals("X") && btn22.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
           
     	     sendMessages("X", message);
              return true;
          }

          else if(btn00.getText().toString().equals("X") && btn11.getText().toString().equals("X") && btn22.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
           
     	     sendMessages("X", message);
              return true;
          }

          if(btn02.getText().toString().equals("X") && btn11.getText().toString().equals("X") && btn20.getText().toString().equals("X"))
          {
        	  String message = "X has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
           
     	     sendMessages("X", message);
              return true;
          }

          else if(btn00.getText().toString().equals("0") && btn01.getText().toString().equals("0") && btn02.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn10.getText().toString().equals("0") && btn11.getText().toString().equals("0") && btn12.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
           
     	     sendMessages("O", message);
              return true;
          }

          else if(btn20.getText().toString().equals("0") && btn21.getText().toString().equals("0") && btn22.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
          
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn00.getText().toString().equals("0") && btn10.getText().toString().equals("0") && btn20.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
         
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn01.getText().toString().equals("0") && btn11.getText().toString().equals("0") && btn21.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn02.getText().toString().equals("0") && btn12.getText().toString().equals("0") && btn22.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
             
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn00.getText().toString().equals("0") && btn11.getText().toString().equals("0") && btn22.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            
     	     sendMessages("O", message);
              return true;
        	  
          }

          else if(btn02.getText().toString().equals("0") && btn11.getText().toString().equals("0") && btn20.getText().toString().equals("0"))
          {
        	  String message = "O has won";
        	  Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            
     	     sendMessages("O", message);
              return true;
        	  
          }
       return false;
    }

    public void checkAgain()
    {
       boolean flag;

       if(!btn00.getText().toString().equals("") && !btn01.getText().toString().equals("") && !btn02.getText().toString().equals("") && !btn10.getText().toString().equals("") && !btn11.getText().toString().equals("") && !btn12.getText().toString().equals("") && !btn20.getText().toString().equals("") && !btn21.getText().toString().equals("") && !btn22.getText().toString().equals(""))
       {
              flag=determineWinner();
              if(!flag)
              {
                     String message = "No One Win , GAME OVER !!!!! ";
                     Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                     sendMessages("tie", message);
              }            
       }
    }
    
      
    class Clicker implements OnClickListener
    {
       boolean flag;
       int turn=1;
       public void onClick(View v)
       {
              playerLbl=(TextView)findViewById(R.id.playerLbl);
              String lbl=playerLbl.getText().toString();
              int position = 0;
              Resources res = v.getResources();
              Button btn=(Button)v;
              int id = btn.getId();
              String sID = res.getResourceEntryName(id);
              System.out.println("string:"+sID);
              switch (sID) {
              case ("btn00"): position = 1; break;
              case ("btn01"): position = 2; break;
              case ("btn02"): position = 3; break;
              case ("btn10"): position = 4; break;
              case ("btn11"): position = 5; break;
              case ("btn12"): position = 6; break;
              case ("btn20"): position = 7; break;
              case ("btn21"): position = 8; break;
              case ("btn22"): position = 9; break;
              default: break;
              }
              System.out.println("id :" + id + "pos: "+ position);
              if(btn.getText().toString().equals(""))
              {
                     if(lbl.equals("Player1"))
                     {
                    	 playerLbl.setText("Player2");
                    	 btn.setText("X");
                    	 System.out.println("Sending X"+ ","+ position);
                         sendMessages("position", Integer.toString(position));
                         sendMessages("value", "X");  
                         sendMessages("turn", Integer.toString(turn));
                         turn++;
                         
                        
                     }
                     else
                     {
                         btn.setText("0");
                         playerLbl.setText("Player1");
                         turn++;
                         System.out.println("Sending O"+ ","+ position);
                         sendMessages("position", Integer.toString(position));
                         sendMessages("value", "O"); 
                         sendMessages("turn", Integer.toString(turn));
                        
                     }
              }
             
              flag=determineWinner();
              if(!flag)
              {
                     checkAgain();
              }            
       }
    }
}