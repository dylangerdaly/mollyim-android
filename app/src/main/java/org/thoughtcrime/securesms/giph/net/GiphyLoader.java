package org.thoughtcrime.securesms.giph.net;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;

import org.signal.core.util.logging.Log;
import org.thoughtcrime.securesms.giph.model.GiphyImage;
import org.thoughtcrime.securesms.giph.model.GiphyResponse;
import org.thoughtcrime.securesms.net.ContentProxySelector;
import org.thoughtcrime.securesms.net.Network;
import org.thoughtcrime.securesms.net.StandardUserAgentInterceptor;
import org.thoughtcrime.securesms.util.AsyncLoader;
import org.thoughtcrime.securesms.util.JsonUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class GiphyLoader extends AsyncLoader<List<GiphyImage>> {

  private static final String TAG = Log.tag(GiphyLoader.class);

  public static int PAGE_SIZE = 100;

  @Nullable private String searchString;

  private final OkHttpClient client;

  protected GiphyLoader(@NonNull Context context, @Nullable String searchString) {
    super(context);
    this.searchString = searchString;
    this.client       = new OkHttpClient.Builder()
                                        .socketFactory(Network.getSocketFactory())
                                        .proxySelector(new ContentProxySelector())
                                        .addInterceptor(new StandardUserAgentInterceptor())
                                        .dns(Network.getDns())
                                        .build();
  }

  @Override
  public List<GiphyImage> loadInBackground() {
    return loadPage(0);
  }

  public @NonNull List<GiphyImage> loadPage(int offset) {
    try {
      String url;

      if (TextUtils.isEmpty(searchString)) url = String.format(getTrendingUrl(), offset);
      else                                 url = String.format(getSearchUrl(), offset, Uri.encode(searchString));

      Request  request  = new Request.Builder().url(url).build();
      Response response = client.newCall(request).execute();

      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }

      GiphyResponse    giphyResponse = JsonUtils.fromJson(response.body().byteStream(), GiphyResponse.class);
      List<GiphyImage> results       = Stream.of(giphyResponse.getData())
                                             .filterNot(g -> TextUtils.isEmpty(g.getGifUrl()))
                                             .filterNot(g -> TextUtils.isEmpty(g.getGifMmsUrl()))
                                             .filterNot(g -> TextUtils.isEmpty(g.getStillUrl()))
                                             .toList();

      if (results == null) return new LinkedList<>();
      else                 return results;

    } catch (IOException e) {
      Log.w(TAG, e);
      return new LinkedList<>();
    }
  }

  protected abstract String getTrendingUrl();
  protected abstract String getSearchUrl();
}
