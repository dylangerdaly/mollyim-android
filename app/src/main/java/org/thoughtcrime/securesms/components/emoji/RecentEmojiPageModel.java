package org.thoughtcrime.securesms.components.emoji;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.util.SecurePreferenceManager;
import org.thoughtcrime.securesms.util.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class RecentEmojiPageModel implements EmojiPageModel {
  private static final String TAG            = Log.tag(RecentEmojiPageModel.class);
  private static final int    EMOJI_LRU_SIZE = 50;

  private final SharedPreferences     prefs;
  private final String                preferenceName;
  private final LinkedHashSet<String> recentlyUsed;

  public RecentEmojiPageModel(Context context, @NonNull String preferenceName) {
    this.prefs          = SecurePreferenceManager.getSecurePreferences(context);
    this.preferenceName = preferenceName;
    this.recentlyUsed   = getPersistedCache();
  }

  private LinkedHashSet<String> getPersistedCache() {
    String serialized = prefs.getString(preferenceName, "[]");
    try {
      CollectionType collectionType = TypeFactory.defaultInstance()
                                                 .constructCollectionType(LinkedHashSet.class, String.class);
      return JsonUtils.getMapper().readValue(serialized, collectionType);
    } catch (IOException e) {
      Log.w(TAG, e);
      return new LinkedHashSet<>();
    }
  }

  @Override public int getIconAttr() {
    return R.attr.emoji_category_recent;
  }

  @Override public List<String> getEmoji() {
    List<String> emoji = new ArrayList<>(recentlyUsed);
    Collections.reverse(emoji);
    return emoji;
  }

  @Override public List<Emoji> getDisplayEmoji() {
    return Stream.of(getEmoji()).map(Emoji::new).toList();
  }

  @Override public boolean hasSpriteMap() {
    return false;
  }

  @Override public String getSprite() {
    return null;
  }

  @Override public boolean isDynamic() {
    return true;
  }

  @MainThread
  public void onCodePointSelected(String emoji) {
    recentlyUsed.remove(emoji);
    recentlyUsed.add(emoji);

    if (recentlyUsed.size() > EMOJI_LRU_SIZE) {
      Iterator<String> iterator = recentlyUsed.iterator();
      iterator.next();
      iterator.remove();
    }

    final LinkedHashSet<String> latestRecentlyUsed = new LinkedHashSet<>(recentlyUsed);
    SignalExecutors.BOUNDED.execute(() -> {
      try {
        String serialized = JsonUtils.toJson(latestRecentlyUsed);
        prefs.edit()
             .putString(preferenceName, serialized)
             .apply();
      } catch (IOException e) {
        Log.w(TAG, e);
      }
    });
  }

  private String[] toReversePrimitiveArray(@NonNull LinkedHashSet<String> emojiSet) {
    String[] emojis = new String[emojiSet.size()];
    int i = emojiSet.size() - 1;
    for (String emoji : emojiSet) {
      emojis[i--] = emoji;
    }
    return emojis;
  }
}
