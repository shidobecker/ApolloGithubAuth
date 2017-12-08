package shido.com.apollogithubauth;

import com.apollographql.apollo.ApolloClient;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Created by mira on 08/12/2017.
 */

public class GraphqlService {

    public static String BASE_GRAPHQL_URL = "https://api.github.com/graphql";

    private static OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

    private static ApolloClient.Builder apolloClient = ApolloClient.builder();


    public static ApolloClient createService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(Level.BODY);

        if (BuildConfig.DEBUG) {
            if (!okHttpClient.interceptors().contains(logging)) {
                okHttpClient.interceptors().add(logging);
            }
        }

        okHttpClient.addInterceptor(chain ->{
           Request origin = chain.request();
           Request.Builder builder = origin.newBuilder()
               .method(origin.method(), origin.body())
               .header("Authorization", "bearer " + BuildConfig.GITHUB_TOKEN);
                return chain.proceed(builder.build());
        }).build();

        okHttpClient.connectTimeout(20, TimeUnit.SECONDS);
        //apolloClient.build().query()
        return apolloClient.okHttpClient(okHttpClient.build())
            .serverUrl(BASE_GRAPHQL_URL)
            .build();
    }



}
