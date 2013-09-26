package com.bluehorn.diamondfusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



public class Sales extends Activity implements OnClickListener {

	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> items_name = new ArrayList<String>();
	ListView listdata;
	Button btnSubmit, btnBack;
	String name, json;
	int check = 0;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	private ProgressDialog pDialog;
	public static int progress_bar_type = 0;
	listviewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnBack = (Button) findViewById(R.id.btn_back);
		listdata = (ListView) findViewById(R.id.listData);
		items = getIntent().getExtras().getStringArrayList("arraylist");
		items_name = getIntent().getExtras().getStringArrayList(
				"arraylist_name");
		check = getIntent().getExtras().getInt("check");
		adapter = new listviewAdapter(this,
				R.layout.list_row, items_name,items);
		listdata.setAdapter(adapter);
		btnSubmit.setOnClickListener(this);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		if (v == btnSubmit) {
			new LongRunningGetIO().execute();
		} else if (v == btnBack) {
			Intent intent = new Intent(Sales.this, FusionMain.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
		}
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Sales.this, FusionMain.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
        
		pDialog = new ProgressDialog(Sales.this);
		pDialog.setMessage("Please wait ...");
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(true);
		pDialog.show();
		return pDialog;
	}
public class listviewAdapter extends BaseAdapter {
		
		
		ArrayList<String> items_nameAdapter = new ArrayList<String>();
		ArrayList<String> items_idAdapter = new ArrayList<String>();
		Activity activity;  LayoutInflater inflater;
		public listviewAdapter(Activity activity, int textViewResourceId, ArrayList<String> items,ArrayList<String> items_id) {
		    super();
		    this.activity = activity;
		    this.items_nameAdapter = items;
		    this.items_idAdapter=items_id;
		    inflater = activity.getLayoutInflater();
		   
		}
		@Override
		public int getCount() {
		    // TODO Auto-generated method stub
		    return items_nameAdapter.size();
		}
		@Override
		public Object getItem(int position) {
		    // TODO Auto-generated method stub
		    return items_nameAdapter.get(position);
		}
		@Override
		public long getItemId(int position) {
		    // TODO Auto-generated method stub
		    return position;
		}
		class ViewHolder {
		    Button btn_del;
		    TextView item_name;
		  
        }
		@Override
		public View getView(final int position, View convertView,
		        final ViewGroup parent) {
		    // TODO Auto-generated method stub
		    final ViewHolder holder;
		        inflater = activity.getLayoutInflater();
		    if (convertView == null) {
		        convertView = inflater.inflate(R.layout.list_row, null);
		        holder = new ViewHolder();
		        holder.btn_del = (Button) convertView.findViewById(R.id.btnD);
		        holder.item_name = (TextView) convertView.findViewById(R.id.tvItems);
		        convertView.setTag(holder);
		    } 
		    else 
		    {
		        holder = (ViewHolder) convertView.getTag();
		    }
		    final String map = items_nameAdapter.get(position);
		    holder.item_name.setText(map);
		    holder.btn_del.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            // TODO Auto-generated method stub
		        	items_nameAdapter.remove(position);
		        	items_idAdapter.remove(position);
		        	adapter.notifyDataSetChanged();
		            listdata.setAdapter(adapter);
		           
		        }
		    });
		    return convertView;
		}
     }

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
		
			super.onPreExecute();
			showDialog(progress_bar_type);
			pDialog.setProgress(0);
		
		}

		@Override
		protected String doInBackground(Void... params) {
		
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(
					"http://creativewebdesignlondon.com/diamond/store/sales.php");
			HttpPost httpPostRefund = new HttpPost(
					"http://creativewebdesignlondon.com/diamond/store/refund.php");
			InputStream stream = null;
			String text = null, Detail = null;
			if (check == 1) {
				Detail = "          Sales Detail";
				for (int i = 0; i < items.size(); i++) {
					text = items.get(i).toString();
					try {
						nameValuePairs.add(new BasicNameValuePair("id", text));
						httpPost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
						HttpResponse response = httpClient.execute(httpPost);
						HttpEntity entity = response.getEntity();
						stream = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(stream, "iso-8859-1"), 8);
						StringBuilder builder = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) {
							builder.append(line + "\n");
						}
						stream.close();
						json = builder.toString();
						Log.d("json", json);
						JSONObject object = new JSONObject(json);
						name = object.getString("msg");
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			          Detail = Detail + "\n" + "Item " + (i + 1) + ": " + name;
				
				}

			} else if (check == 2) {
				
				Detail = "          Refund Detail";
				for (int i = 0; i < items.size(); i++) {
					text = items.get(i).toString();
					try {
						nameValuePairs.add(new BasicNameValuePair("id", text));
						httpPostRefund.setEntity(new UrlEncodedFormEntity(
								nameValuePairs));
						HttpResponse response = httpClient
								.execute(httpPostRefund);
						HttpEntity entity = response.getEntity();
						stream = entity.getContent();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(stream, "iso-8859-1"), 8);
						StringBuilder builder = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) {
							builder.append(line + "\n");
						}
						stream.close();
						json = builder.toString();
						Log.d("json", json);
						JSONObject object = new JSONObject(json);
						name = object.getString("msg");
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Detail = Detail + "\n" + "Item " + (i + 1) + ": " + name;
				}
			}
			return Detail;
		
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String results) {
		
			if (results != null) {
				dismissDialog(progress_bar_type);

				AlertDialog.Builder builder = new AlertDialog.Builder(Sales.this);
                builder.setTitle("Diamond Fusion");
			    builder.setMessage(results);
	            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
	                @Override
			        public void onClick(DialogInterface dialog, int which) {
			            // Do nothing
			            dialog.dismiss();
			        }
			    });
	            AlertDialog alert = builder.create();
			    alert.show();
				
			}
		}
	}
}
