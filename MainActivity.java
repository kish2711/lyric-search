package com.example.lyricsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final int FILE_SELECT_CODE = 0;
	public String fileout,fileout1, picturePath;
	private LinearLayout ll;
	private ListView listView;
	private EditText find;
	private TextView result;
	private Button btn, listbtn;
	private Context context = this;
	public String items;
	ArrayAdapter<String> adapter;
	ArrayList<String> txtlist = new ArrayList<String>();
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> mp3list = new ArrayList<String>();

	static HashMap<String, String> song_path = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ll = (LinearLayout) findViewById(R.id.linear);

		listView = (ListView) findViewById(R.id.listView1);
		find = (EditText) findViewById(R.id.find_text);
		result = (TextView) findViewById(R.id.result);
		btn = (Button) findViewById(R.id.search);
		listbtn = (Button) findViewById(R.id.list);

		listbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// listView.setVisibility(View.VISIBLE);
				showFileChooser();

			}
		});

		try {
			fileout1 = Environment.getExternalStorageDirectory() + "/ReadFile/";
			File filedire = new File(fileout1);
			if (!filedire.exists()) {
				filedire.mkdirs();
			} else {
				File temp = new File(fileout1);
				File[] count = temp.listFiles();

				for (int i = 0; i < count.length; i++) {
					String st = count[i].getName();
					list.add(st);
				}
			}

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "-" + e, Toast.LENGTH_SHORT)
					.show();
		}
		
		try {
			fileout = Environment.getExternalStorageDirectory() + "/MP3File/";
			File filedire = new File(fileout);
			if (!filedire.exists()) {
				filedire.mkdirs();
			} else {
				File temp = new File(fileout);
				File[] count = temp.listFiles();

				for (int i = 0; i < count.length; i++) {
					String st = count[i].getName();
					mp3list.add(st);
				}
			}

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "-" + e, Toast.LENGTH_SHORT)
					.show();
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mp3list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String name = mp3list.get(arg2);
				Toast.makeText(getApplicationContext(), "--Song name--"+name, Toast.LENGTH_LONG).show();
				String filePath = Environment.getExternalStorageDirectory()+"/MP3File/"+name;
				Intent intent = new Intent();  
				intent.setAction(android.content.Intent.ACTION_VIEW);  
				File file = new File(filePath);  
				intent.setDataAndType(Uri.fromFile(file), "audio/*");  
				startActivity(intent);
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (find.getText().toString().equals("")) {
					find.setError("Please Enter Keyword");
				} else {
					String s = find.getText().toString();
					Log.e("find ", s);
					txtlist.clear();
					result.setText("");
					Log.e("ArrayList", txtlist.toString());
					asunkTask();

				}
			}
		});
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String name = "", format = "";
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Log.e("File Uri: ", "" + uri.toString());
				try {
					String path = getPath(this, uri);
					// Toast.makeText(getBaseContext(), path,
					// Toast.LENGTH_SHORT);
					// showDialogBox("Select Installation Mode",items);
					File file = new File(path);
					

					String s = file.getName();
					
					int i = s.lastIndexOf(".");
//					String temp = s.substring(0, i);
					String for_mat = s.substring(i + 1);
					Log.e("format", for_mat);
					if (!for_mat.equalsIgnoreCase("mp3")) {
						
						File file2 = new File(fileout1+s);
						try {
							FileUtils.copyFile(file, file2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// Toast.makeText(getBaseContext(), "Not A MP3 Format",
						// Toast.LENGTH_LONG).show();
						list.add(s);
						Toast.makeText(getBaseContext(), s + " was uploaded",
								Toast.LENGTH_LONG).show();
					}

					else {
						
						File file2 = new File(fileout+s);
						try {
							FileUtils.copyFile(file, file2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(!mp3list.contains(s))
						{
						mp3list.add(s);
						}
						song_path.put(s, path);
						
						Toast.makeText(getBaseContext(), s + " was uploaded",
								Toast.LENGTH_LONG).show();
						adapter.notifyDataSetChanged();

						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("Confirmation........");
						builder.setMessage("Are You Want To Upload Any  Lyrics ...?");
						builder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										show();
									}
								});
						builder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
						builder.create();
						builder.show();
						
						

					}
					
					
					

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void show() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showDialogBox(String string, String items2) {
		// TODO Auto-generated method stub

	}

	public String getPath(Context context, Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				Log.e("AppSelection", e.toString());
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public void asunkTask() {

		class UploadVideo extends AsyncTask<Void, Void, String> {

			ProgressDialog uploading;
			String songname="";

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				uploading = ProgressDialog.show(MainActivity.this,
						"Searching...", "Please wait...", false, false);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
			String songlist= "";
				if (s.equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("Alert!");
					builder.setIcon(R.drawable.alert);
					builder.setMessage("Not found song...");
			          
			                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			                            @Override
			                            public void onClick(DialogInterface dialog,int which) {
			                                dialog.dismiss();
			                            }
			                        });
			                builder.create();
							builder.show();
//					result.setText("No Record Found");
				} else if (s.contains("@")) {
					StringTokenizer str=new StringTokenizer(s,"@");
					while(str.hasMoreTokens())
					{
						String txtfile=str.nextToken().trim();
						txtfile=txtfile.substring(0,txtfile.length()-4);
						if(!txtlist.contains(txtfile.trim())){
							txtlist.add(txtfile.trim());	
						}
						
						Log.e("find song", txtlist.toString());
						
					}
					Iterator<String> it=txtlist.iterator();
					while(it.hasNext())
					{
						String filename=it.next();
						Log.e("find song file name ",filename);
					for(Object ma2:mp3list)
					{
						songname=ma2.toString();
						int i = songname.lastIndexOf(".");
						String temp=songname.substring(0,i);
						Log.e("Srs file name", temp);
						Log.e("dest file name", filename);
						if(temp.equalsIgnoreCase(filename))
						{
							songlist=songlist+songname+"\n";
						}
					}
					}
//							result.setText(songlist);
							AlertDialog.Builder builder = new AlertDialog.Builder(
									context);
							builder.setTitle("SongList");
							builder.setIcon(R.drawable.icon);
							builder.setMessage(songlist);
					          
					                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					                            @Override
					                            public void onClick(DialogInterface dialog,int which) {
					                                dialog.dismiss();
					                            }
					                        });
					           
						
//							builder.setNegativeButton("Cancel",
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(DialogInterface dialog,
//												int which) {
//											// TODO Auto-generated method stub
//											dialog.dismiss();
//										}
//									});
							builder.create();
							builder.show();
							
							
							
							
							
						
//						else
//						{
//							Toast.makeText(getApplicationContext(), "does not match", Toast.LENGTH_SHORT).show();
//						}
						
					
					
				
				}else {
					result.setText(songname);
				}
				uploading.dismiss();

			}

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					Log.e("find ", "Inside Asyn");
					
					Search u = new Search();
					msg = u.file();
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				return msg;
			}
		}
		UploadVideo uv = new UploadVideo();
		uv.execute();

	}

	class Search {
		public String file() {
			String temp = "";
			for (Object object : list) {
				try {
					Log.e("find ", "Inside for");
					File file = new File(fileout1, object.toString());
					FileInputStream fis = new FileInputStream(file);
					byte[] data = new byte[(int) file.length()];
					fis.read(data);
					fis.close();

					String str = new String(data, "UTF-8");
					            System.out.println("str : "+str);
					   
					    
//					try {
//						BufferedReader br = new BufferedReader(new FileReader(
//								file));
//						String line;
//						Log.e("0909", file.toString());
//						while ((line = br.readLine()) != null) {
							if (str.contains(find.getText().toString())) {
								temp = temp + "@" + object.toString();
								Log.e("find ", temp);
								//break;
							}
//						}
//						br.close();
					} catch (IOException e) {
						// You'll need to add proper error handling here
						Toast.makeText(getApplicationContext(), "-" + e,
								Toast.LENGTH_SHORT).show();
					}

//				} catch (Exception e) {
//
//				}

			}
			return temp;
		}
	}

}
