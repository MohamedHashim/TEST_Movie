package com.example.mohamedhashim.hashim;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvData;
    private  ListView lvMovies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         lvMovies = (ListView) findViewById(R.id.lvMovies);

        //   new JSONTask().execute("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5bf92cd209aa47161a39f6ab96f0e0fe&append_to_response=images&include_image_language=en,null");

    }

    public class JSONTask extends AsyncTask<String, String, List<Result> > {

        @Override
        protected List<Result>  doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                //PARSING

                String finalJson=buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray= parentObject.getJSONArray("results");
                List<Result> movieModelList=new ArrayList<>();

                for (int i=0; i< parentArray.length();i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    Result moviemodel = new Result();
                    moviemodel.setTitle(finalObject.getString("title"));
                    moviemodel.setReleaseDate(finalObject.getString("release_date"));
                    moviemodel.setPopularity(finalObject.getDouble("popularity"));
                    moviemodel.setOverview(finalObject.getString("overview"));
                    moviemodel.setPosterPath(finalObject.getString("poster_path"));
                    // adding  the final object in the list
                    movieModelList.add(moviemodel);
                }
                return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Result>  result) {
            //TODO need to set data to the list
            super.onPostExecute(result);
            MovieAdapter adapter=new MovieAdapter(getApplicationContext(),R.layout.row,result);
            lvMovies.setAdapter(adapter);
        }
    }

public class MovieAdapter extends ArrayAdapter{
    private List<Result> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public MovieAdapter(Context context, int resource, List<Result> objects) {
        super(context, resource, objects);
        movieModelList=objects;
        this.resource=resource;
        inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(resource,null);
        }
        ImageView ivMovieIcon;
        TextView tvMovie;
        TextView tvTagline;
        TextView tvYear;
        TextView tvDuration;
        TextView tvDirector;
        RatingBar rbMovieRating;
        TextView tvCast;
        TextView tvStory;

        ivMovieIcon= (ImageView) findViewById(R.id.ivIcon);
        tvMovie= (TextView) findViewById(R.id.tvMovie);
        tvTagline= (TextView) findViewById(R.id.tvTagline);
        tvYear= (TextView) findViewById(R.id.tvYear);
        tvDuration= (TextView) findViewById(R.id.tvDuration);
        tvDirector= (TextView) findViewById(R.id.tvDirector);
        rbMovieRating= (RatingBar) findViewById(R.id.rbMovie);
        tvCast= (TextView) findViewById(R.id.tvCast);
        tvStory= (TextView) findViewById(R.id.tvStory);

      //  tvMovie.setText(movieModelList.get(position).getTitle());
     //   tvTagline.setText(movieModelList.get(position).getTitle());
       // tvYear.setText(movieModelList.get(position).getReleaseDate());
     /* //  tvDuration.setText(movieModelList.get(position).getTitle());
       // tvDirector.setText(movieModelList.get(position).getTitle());

        rbMovieRating.setRating((float) (movieModelList.get(position).getPopularity()/.2));

       // tvCast.setText(movieModelList.get(position).getTitle());
        tvStory.setText(movieModelList.get(position).getOverview());
*/
        return convertView;
    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        if(id==R.id.action_refresh){//TODO check the latest api link again
            new JSONTask().execute("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5bf92cd209aa47161a39f6ab96f0e0fe&append_to_response=images&include_image_language=en,null");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}