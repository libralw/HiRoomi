package mobile08723.team10.hiroomi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class DetailActivity extends ActionBarActivity {

    TextView comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();


        //ParseProxyObject ppo = (ParseProxyObject) intent.getSerializableExtra("po");
        setTitle(intent.getStringExtra("title"));

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PostInfo");
        query.whereEqualTo("objectId", intent.getStringExtra("id"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> apList, ParseException e) {
                if (e == null) {
                    Log.d("HiRoomi", "Retrieved " + apList.size() + " Infos");
                    for (ParseObject ap : apList) {
                        setDetail(ap);

                        String url =String.format("%s/%s/ratings?access_token=%s","https://graph.facebook.com/v2.4",ap.get("commentID"),ap.get("token"));
                        Log.d("HiRoomi",url);

                        new TheTask().execute(url);
                                //HttpGet httpGet = new HttpGet(url);
//                httpGet.setHeader("Host","http://maps.googleapis.com");
//                        try {
//                            // create HttpClient
//                            HttpClient httpclient = new DefaultHttpClient();
//                        HttpResponse hresponse=httpclient.execute(httpGet);
//                            HttpEntity hEntity=hresponse.getEntity();
//                            String response = EntityUtils.toString(hEntity);
//                            //got the json now parse and display on the screen the required things
//                            JSONObject jsonObj = new JSONObject(response);
//                            Log.d("HiRoomi",jsonObj.toString());
//                            JSONArray data=jsonObj.getJSONArray("data");
//                            for (int i=0;i<data.length();i++){
//                               JSONObject jb= (JSONObject)(data.get(i));
//                                Log.d("HiRoomi", jb.getString("review_text"));
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }





                    }
                } else {
                    Log.d("HiRoomi", "Error: " + e.getMessage());
                }
            }
        });
//        ParseFile image = (ParseFile) ppo.getValues().get("Image");
//        ImageView image_expert = (ImageView)findViewById(R.id.imageView);
//        displayImage(image, image_expert);
        // Add and download the image
//        ParseFile imageFile = (ParseFile)ppo.getValues().get("Image");
//
//        ParseImageView todoImage = (ParseImageView) this.findViewById(R.id.icon);
//        ParseFile imageFile = ppo.getFile("Image");
//        if (imageFile != null) {
//            todoImage.setParseFile(imageFile);
//            todoImage.loadInBackground();
//        }

    }

    public void setDetail (ParseObject po){
        ParseFile imageFile = (ParseFile)po.getParseFile("Image");

        ParseImageView todoImage = (ParseImageView) this.findViewById(R.id.icon);

        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }
        TextView price = (TextView) this.findViewById(R.id.price);

        price.setText(String.format(" $%d ",po.getInt("Price")));


        TextView available = (TextView) this.findViewById(R.id.textView);
        available.setText(String.format("available from %d/%d/%d to %d/%d/%d", po.getInt("FromDay"), po.getInt("FromMonth")
                , po.getInt("FromYear"), po.getInt("ToDay"), po.getInt("ToMonth"), po.getInt("ToYear")));

        TextView upload = (TextView) this.findViewById(R.id.textView2);
        upload.setText(String.format("Uploaded by %s", po.getString("UserName")));

        TextView tenant = (TextView) this.findViewById(R.id.textView3);
        tenant.setText(String.format("Need %d tenants", po.getInt("NeedTenant")));

        TextView description = (TextView) this.findViewById(R.id.textView4);
        description.setText(String.format("%s", po.getString("description")));

        comment = (TextView) this.findViewById(R.id.textView5);

        description.setMovementMethod(new ScrollingMovementMethod());
        comment.setMovementMethod(new ScrollingMovementMethod());
        //description.setText(String.format("Description:%s", po.getString("description")));


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", po.getString("UserName"));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    Log.d("HiRoomi", "Retrieved " + objects.size() + " Infos");
                    for (ParseUser ap : objects) {
                        ParseFile imageFile = (ParseFile) ap.getParseFile("Image");

                        ParseImageView todoImage = (ParseImageView) DetailActivity.this.findViewById(R.id.profile);

                        if (imageFile != null) {
                            todoImage.setParseFile(imageFile);
                            todoImage.loadInBackground();
                        }
                    }
                } else {
                    Log.d("HiRoomi", "Error: " + e.getMessage());
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void displayImage(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {

                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                data.length);

                        if (bmp != null) {

                            Log.e("parse file ok", " null");
                            // img.setImageBitmap(Bitmap.createScaledBitmap(bmp,
                            // (display.getWidth() / 5),
                            // (display.getWidth() /50), false));
                            img.setImageBitmap(bmp);
                            // img.setPadding(10, 10, 0, 0);



                        }
                    } else {
                        Log.e("paser after downloade", " null");
                    }

                }
            });
        } else {

            Log.e("parse file", " null");

            // img.setImageResource(R.drawable.ic_launcher);

            img.setPadding(10, 10, 10, 10);
        }

    }
    class TheTask extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // update textview here
            StringBuffer sb = new StringBuffer();
            try {
                                        JSONObject jsonObj = new JSONObject(result);
                            Log.d("HiRoomi",jsonObj.toString());
                            JSONArray data=jsonObj.getJSONArray("data");
                            for (int i=0;i<data.length();i++){
                               JSONObject jb= (JSONObject)(data.get(i));
                                Log.d("HiRoomi", jb.getString("review_text"));
                                JSONObject reviewer = (JSONObject)(jb.getJSONObject("reviewer"));
                                sb.append(reviewer.getString("name"));
                                sb.append("\t");
                                sb.append(String.valueOf(jb.getInt("rating")));
                                sb.append("\n");
                                sb.append(jb.getString("review_text"));
                                sb.append("\n");

                            }
            } catch (Exception ex) {
                            ex.printStackTrace();
                        }
            comment.setText(sb);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet method = new HttpGet(params[0]);
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    return EntityUtils.toString(entity);
                }
                else{
                    return "No string.";
                }
            }
            catch(Exception e){
                return "Network problem";
            }

        }
    }
}
