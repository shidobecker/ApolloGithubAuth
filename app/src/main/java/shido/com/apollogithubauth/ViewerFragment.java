package shido.com.apollogithubauth;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import shido.com.apollogithubauth.Whoami.Data;

/**
 * Created by mira on 08/12/2017.
 */

public class ViewerFragment extends RecyclerViewFragment {
    private Observable<Whoami.Data> observable;
    private Disposable sub;
    private ApolloClient apolloClient = GraphqlService.createService();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        observable = Rx2Apollo.from(apolloClient.query(new Whoami()).watcher())
            .subscribeOn(Schedulers.io())
            .map(response -> (getFields(response)))
            .cache()
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        setLayoutManager(new LinearLayoutManager(getActivity()));

        getRecyclerView()
            .addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));

        unsub();
        sub=observable.subscribe(
            s -> setLogin(s.viewer().login()),
            error -> {
                Toast
                    .makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG)
                    .show();
                Log.e(getClass().getSimpleName(), "Exception processing request",
                    error);
            });
    }

    @Override
    public void onDestroy() {
        unsub();

        super.onDestroy();
    }

    private void unsub() {
        if (sub!=null && !sub.isDisposed()) {
            sub.dispose();
        }
    }

    private Whoami.Data getFields(Response<Whoami.Data> response) {
        if (response.hasErrors()) {
            throw new RuntimeException(response.errors().get(0).message());
        }

        return(response.data());
    }

    private void setLogin(String login) {
        ((MainActivity)getActivity()).setLogin(login);
    }


}
