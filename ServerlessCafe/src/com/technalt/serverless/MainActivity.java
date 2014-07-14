package com.technalt.serverless;



import java.util.List;



import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.technalt.serverlessCafe.R;


public class MainActivity extends Activity implements Observer {

	

	private Button join;
	private Button stop;
	private Button start;
	private Button leave;
	private Button sendjson;
	
	

	private CafeApplication mChatApplication = null;
	private TextView preview;
	private EditText edit;

	private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
	private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 1;
	private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.host);

		start = (Button) findViewById(R.id.startchannel);
		stop = (Button) findViewById(R.id.stopchannel);
		join = (Button) findViewById(R.id.joinchannel);
		
		preview = (TextView) findViewById(R.id.textpreview);
		edit = (EditText) findViewById(R.id.editpreview);
		
		
		sendjson = (Button) findViewById(R.id.sendjson);
		
		leave = (Button) findViewById(R.id.leavechannel);
		
		
		
		stop.setEnabled(false);
		sendjson.setEnabled(false);
		leave.setEnabled(false);
		
		
		sendjson.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				
			String s = 	edit.getText().toString() +"";				
				
				  mChatApplication.newLocalUserMessage(s);
				  
				  edit.setText("");
				
			
				
			}
		});
		

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mChatApplication.hostSetChannelName("ServerlessCafe");
				mChatApplication.hostInitChannel();
				mChatApplication.hostStartChannel();
				
				
				start.setEnabled(false);
				
				stop.setEnabled(true);

			}
		});

		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChatApplication.hostStopChannel();
				
				stop.setEnabled(false);
				start.setEnabled(true);
				leave.setEnabled(false);
				sendjson.setEnabled(false);
				
				
			}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			
				  
				  
				  final Dialog dialog = new Dialog(MainActivity.this);
			    	dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
			    	dialog.setContentView(R.layout.usejoindialog);
			    	
			        ArrayAdapter<String> channelListAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.test_list_item);
			    	final ListView channelList = (ListView)dialog.findViewById(R.id.useJoinChannelList);
			        channelList.setAdapter(channelListAdapter);
			        
				    List<String> channels = mChatApplication.getFoundChannels();
			        for (String channel : channels) {
			        	int lastDot = channel.lastIndexOf('.');
			        	if (lastDot < 0) {
			        		continue;
			        	}
			            channelListAdapter.add(channel.substring(lastDot + 1));
			        }
				    channelListAdapter.notifyDataSetChanged();
			    	
			    	channelList.setOnItemClickListener(new ListView.OnItemClickListener() {
			    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    			String name = channelList.getItemAtPosition(position).toString();
							mChatApplication.useSetChannelName(name);
							mChatApplication.useJoinChannel();
							
							
							start.setEnabled(false);
							stop.setEnabled(false);
							join.setEnabled(false);
							sendjson.setEnabled(true);
							leave.setEnabled(true);
							
			    			dialog.dismiss();
			    		}
			    	});
			    	        	           
			    	Button cancel = (Button)dialog.findViewById(R.id.useJoinCancel);
			    	cancel.setOnClickListener(new View.OnClickListener() {
			    		public void onClick(View view) {
							
			    			dialog.dismiss();
			    		}
			    	});
				  
				  
				  
				  dialog.show();
				  
				  
				  
				
				
	        	

			}
		});

		leave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			


				
				mChatApplication.useLeaveChannel();
				mChatApplication.useSetChannelName("Not set");
				leave.setEnabled(false);
				sendjson.setEnabled(false);
				
				//start.setEnabled(true);
				stop.setEnabled(true);
				join.setEnabled(true);
				
				
				
			}
		});

		mChatApplication = (CafeApplication) getApplication();
		mChatApplication.checkin();

		updateChannelState();

		mChatApplication.addObserver(this);

	}

	public void onDestroy() {
		
		mChatApplication = (CafeApplication) getApplication();
		mChatApplication.deleteObserver(this);
		
		
		 mChatApplication.quit();
		
		super.onDestroy();
	}

	private void updateChannelState() {
		AllJoynService.HostChannelState channelState = mChatApplication
				.hostGetChannelState();
		String name = mChatApplication.hostGetChannelName();
		boolean haveName = true;
		if (name == null) {
			haveName = false;
			name = "Not set";
		}

		Toast.makeText(MainActivity.this, "Session Name " + name,
				Toast.LENGTH_SHORT).show();

		switch (channelState) {
		case IDLE:

			Toast.makeText(MainActivity.this, "Session Status idle",
					Toast.LENGTH_SHORT).show();

			break;
		case NAMED:

			Toast.makeText(MainActivity.this, "Session status named" + name,
					Toast.LENGTH_SHORT).show();
			break;
		case BOUND:

			Toast.makeText(MainActivity.this, "Session status bound" + name,
					Toast.LENGTH_SHORT).show();

			break;
		case ADVERTISED:

			Toast.makeText(MainActivity.this,
					"Session status advertised" + name, Toast.LENGTH_SHORT)
					.show();
			break;
		case CONNECTED:

			Toast.makeText(MainActivity.this, "Session status connected",
					Toast.LENGTH_SHORT).show();

			break;
		default:

			Toast.makeText(MainActivity.this, "Session status unknown",
					Toast.LENGTH_SHORT).show();

			break;
		}

		if (channelState == AllJoynService.HostChannelState.IDLE) {

		}

	}
	
	
	
	 private void updateHistory() {
		 
		 
		 
		 String messager = mChatApplication.getHistoryMessage();
		 
			preview.setText(messager);
		 
	      
		/* List<String> messages = mChatApplication.getHistory();
	        for (String message : messages) {
	        	 Toast.makeText(MainActivity.this, "History changed!!" + message, Toast.LENGTH_SHORT).show();
	        }*/
		
		 
	    }
	
	
	 private static final int HANDLE_HISTORY_CHANGED_EVENT = 3;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			
	           
         case HANDLE_HISTORY_CHANGED_EVENT:
             {
                 Log.i("", "mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
                 updateHistory();
                 break;
             }
        
			case HANDLE_APPLICATION_QUIT_EVENT: {

				finish();
			}
				break;
			case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {

				updateChannelState();
			}
				break;
			case HANDLE_ALLJOYN_ERROR_EVENT: {

			}
				break;
			default:
				break;
			}
		}
	};

	public synchronized void update(Observable o, Object arg) {

		String qualifier = (String) arg;

		if (qualifier.equals(CafeApplication.APPLICATION_QUIT_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
			mHandler.sendMessage(message);
		}

		
		 if (qualifier.equals(CafeApplication.HISTORY_CHANGED_EVENT)) {
	            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
	            mHandler.sendMessage(message);
	        }
		
		if (qualifier.equals(CafeApplication.HOST_CHANNEL_STATE_CHANGED_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
			mHandler.sendMessage(message);
		}

		if (qualifier.equals(CafeApplication.ALLJOYN_ERROR_EVENT)) {
			Message message = mHandler
					.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
			mHandler.sendMessage(message);
		}
	}

}
