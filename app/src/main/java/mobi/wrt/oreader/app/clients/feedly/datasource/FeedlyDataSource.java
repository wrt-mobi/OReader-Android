package mobi.wrt.oreader.app.clients.feedly.datasource;

import android.net.ParseException;
import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.feedly.exception.FeedlyAuthException;

public class FeedlyDataSource extends HttpAndroidDataSource {

    public static final String APP_SERVICE_KEY = "oreader:feedly:httpdatasource";

    public FeedlyDataSource() {
        super(new DefaultHttpRequestBuilder(){
            @Override
            public HttpRequestBase build(DataSourceRequest dataSourceRequest) throws IOException {
                HttpRequestBase httpRequestBase = super.build(dataSourceRequest);
                try {
                    AuthManagerFactory.get(ContextHolder.get()).getManager(AuthManagerFactory.Type.FEEDLY).sign(httpRequestBase);
                } catch (Exception e) {
                    throw new IOException(e);
                }
                return httpRequestBase;
            }

            @Override
            protected HttpRequestBase createPostRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
                HttpPost postRequest = new HttpPost(url.split(Q)[0]);
                try {
                    postRequest.setEntity(new StringEntity(PostDataSourceRequest.getBody(dataSourceRequest), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    //really can be happens?
                }
                return postRequest;
            }
        }, new DefaultResponseStatusHandler() {
            @Override
            public void statusHandle(HttpAndroidDataSource dataSource, HttpUriRequest request, HttpResponse response) throws IOStatusException, ParseException, IOException {
                if (response.getStatusLine().getStatusCode() == 403) {
                    throw new FeedlyAuthException();
                }
                super.statusHandle(dataSource, request, response);
            }
        });
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
