package com.bluehorn.diamondfusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class FusionMain extends Activity implements
		android.view.View.OnClickListener {
	Button btnSales,btnRefund,btnContinue,btnFinish,btnBack;
	ListView list;
	String name, json;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	private ProgressDialog pDialog;
	public static int progress_bar_type = 0;
	ArrayList<String> items = new ArrayList<String>();
	ArrayList<String> items_name = new ArrayList<String>();
    listviewAdapter adapter;
	int check = 0;   Boolean isInternetPresent = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fusion_main);
		btnSales = (Button) findViewById(R.id.btnSales);
		btnRefund = (Button) findViewById(R.id.btnRefund);
		btnContinue = (Button) findViewById(R.id.btnContinue);
		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnBack = (Button) findViewById(R.id.btnBacki);
		list = (ListView)findViewById(R.id.main_list);
        list.setAdapter(null);
        btnContinue.setVisibility(View.GONE);
		btnFinish.setVisibility(View.GONE);
		btnBack.setVisibility(View.GONE);
		list.setVisibility(View.GONE);
        btnFinish.setOnClickListener(this);
		btnContinue.setOnClickListener(this);
		btnSales.setOnClickListener(this);
		btnRefund.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		if(isNetworkOnline() == false){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

		    builder.setTitle("No Internet Connection");
		    builder.setMessage("Please Connect to Internet");
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

	@Override
	public void onClick(View v) {
		if (v == btnSales) {
			IntentIntegrator integrator = new IntentIntegrator(FusionMain.this);
			integrator.initiateScan();
			check = 1;
		} else if (v == btnRefund) {
			IntentIntegrator integrator = new IntentIntegrator(FusionMain.this);
			integrator.initiateScan();
			check = 2;
		} else if (v == btnContinue) {
			IntentIntegrator integrator = new IntentIntegrator(FusionMain.this);
			integrator.initiateScan();
		} else if (v == btnFinish) {
			new LongRunningGetIO().execute();
		}
		else if(v==btnBack){
			btnContinue.setVisibility(View.GONE);
			btnFinish.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			list.setAdapter(null);
			items.clear();
			items_name.clear();
			list.setVisibility(View.GONE);
			btnSales.setVisibility(1);
			btnRefund.setVisibility(1);
			
		}
	
	}
	
	 public boolean isNetworkOnline() {
		    
		    try{
		        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo netInfo = cm.getNetworkInfo(0);
		        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
		        	isInternetPresent= true;
		        }else {
		            netInfo = cm.getNetworkInfo(1);
		            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
		            	isInternetPresent= true;
		        }
		    }catch(Exception e){
		        e.printStackTrace();  
		        return false;
		    }
		    return isInternetPresent;

		    }  
	
	@Override
	protected Dialog onCreateDialog(int id) {

		pDialog = new ProgressDialog(FusionMain.this);
		pDialog.setMessage("Please wait ...");
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(true);
		pDialog.show();
		return pDialog;
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle("Exit Diamond Fusion");
	    builder.setMessage("Are you sure?");

	    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing but close the dialog

	            finish();
	        }

	    });

	    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            // Do nothing
	            dialog.dismiss();
	        }
	    });

	    AlertDialog alert = builder.create();
	    alert.show();
		
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
					"http://creativewebdesignlondon.com/diamond/store/productinfo.php");
			InputStream stream = null;
			String text = null;
			for (int i = 0; i < items.size(); i++) {
				text = items.get(i).toString();
				try {
					nameValuePairs.add(new BasicNameValuePair("id", text));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
					items_name.add(name);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "1";
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String results) {
			if (results != null) {
				dismissDialog(progress_bar_type);
				Intent intent = new Intent(FusionMain.this, Sales.class);
				intent.putExtra("arraylist", items);
				intent.putExtra("arraylist_name", items_name);
				intent.putExtra("check", check);
				startActivity(intent);
			}
        }
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanResult != null)
			if (scanResult.getContents() != null
					&& scanResult.getContents().length() > 0) {
				items.add(scanResult.getContents());
			    adapter = new listviewAdapter(this, R.layout.list_row, items);
                btnContinue.setVisibility(1);
				btnFinish.setVisibility(1);
				btnBack.setVisibility(1);
				list.setVisibility(1);
				btnSales.setVisibility(View.GONE);
				btnRefund.setVisibility(View.GONE);
				list.setAdapter(adapter);
				
				
			}
      }
public class listviewAdapter extends BaseAdapter {
		
		
		ArrayList<String> items_adapter = new ArrayList<String>();
		Activity activity;  LayoutInflater inflater;
		public listviewAdapter(Activity activity, int textViewResourceId, ArrayList<String> items) {
		    super();
		    this.activity = activity;
		    this.items_adapter = items;
		    inflater = activity.getLayoutInflater();
		   
		}
		@Override
		public int getCount() {
		    // TODO Auto-generated method stub
		    return items_adapter.size();
		}
		@Override
		public Object getItem(int position) {
		    // TODO Auto-generated method stub
		    return items_adapter.get(position);
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
		    final String map = items_adapter.get(position);
		    holder.item_name.setText(map);
		    holder.btn_del.setOnClickListener(new View.OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            // TODO Auto-generated method stub
		        	items_adapter.remove(position);
		        	
		            adapter.notifyDataSetChanged();
                    list.setAdapter(adapter);
		        }
		    });
		    return convertView;
		}
     }
  }
