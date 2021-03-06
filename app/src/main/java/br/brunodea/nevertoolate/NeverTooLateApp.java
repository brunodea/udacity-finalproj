package br.brunodea.nevertoolate;

import android.app.Application;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import net.dean.jraw.RedditClient;
import net.dean.jraw.android.AndroidHelper;
import net.dean.jraw.android.AppInfoProvider;
import net.dean.jraw.android.ManifestAppInfoProvider;
import net.dean.jraw.android.SharedPreferencesTokenStore;
import net.dean.jraw.oauth.AccountHelper;

import java.util.UUID;

public class NeverTooLateApp extends Application {
    private static AccountHelper mAccountHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesTokenStore tokenStore = new SharedPreferencesTokenStore(getApplicationContext());
        tokenStore.load();
        tokenStore.setAutoPersist(true);

        AppInfoProvider provider = new ManifestAppInfoProvider(getApplicationContext());
        UUID deviceUuid = UUID.randomUUID();
        mAccountHelper = AndroidHelper.accountHelper(provider, deviceUuid);
    }

    private static RedditClient sRedditClient;
    public static RedditClient redditClient() {
        if (sRedditClient == null) {
            sRedditClient = mAccountHelper.switchToUserless();
            sRedditClient.setAutoRenew(true);
        }
        return sRedditClient;
    }

    private static GoogleApiClient sGoogleApiClient;
    public static GoogleApiClient googleClient(FragmentActivity activity, GoogleApiClient.OnConnectionFailedListener failure_callback) {
        if (sGoogleApiClient == null) {
            sGoogleApiClient = new GoogleApiClient
                    .Builder(activity)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(activity, failure_callback)
                    .build();
        }
        return sGoogleApiClient;
    }
}
