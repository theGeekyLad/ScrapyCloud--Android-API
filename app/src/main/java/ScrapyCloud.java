import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ScrapyCloud {

    private String projectId;
    private String apiKey;
    private Context context;

    public ScrapyCloud(String projectId,
                       String apiKey,
                       Context context) {
        this.projectId = projectId;
        this.apiKey = apiKey;
        this.context = context;
    }

    public void get(String url, String path, Map<String, String> spiderArgs,
                    Response.Listener<String> listener,
                    Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url + path + prepareQuery(spiderArgs),
                listener,
                errorListener
        );
        requestQueue.add(request);
    }

    private String prepareQuery(Map<String, String> spiderArgs) {
        String query = "?project=" + projectId + "&apikey=" + apiKey;
        if (spiderArgs != null)
            for (String paramKey : spiderArgs.keySet())
                query += "&" + paramKey + "=" + spiderArgs.get(paramKey);
        return query;
    }

    public void post(String url, String path, final String spider, final Map<String, String> spiderArgs, final Map<String, String> headerArgs,
                     Response.Listener<String> listener,
                     Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url + path,
                listener,
                errorListener
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("project", projectId);
                params.put("spider", spider);
                if (spiderArgs != null)
                    for (String arg : spiderArgs.keySet())
                        params.put(arg, spiderArgs.get(arg));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                try {
                    headers.put("Authorization", "Basic " + URLEncoder.encode(Base64.encodeToString((apiKey + ":").getBytes(), Base64.NO_WRAP), "UTF-8"));
                    if (headerArgs != null)
                        for (String headerKey : headerArgs.keySet())
                            headers.put(headerKey, URLEncoder.encode(Base64.encodeToString((headers.get(headerKey)).getBytes(), Base64.NO_WRAP), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                }
                return headers;
            }
        };
        requestQueue.add(request);
    }

}