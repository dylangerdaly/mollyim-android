package org.thoughtcrime.securesms.webrtc;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.signal.core.util.logging.Log;
import org.thoughtcrime.securesms.PassphraseRequiredActivity;
import org.thoughtcrime.securesms.WebRtcCallActivity;
import org.thoughtcrime.securesms.dependencies.ApplicationDependencies;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.util.concurrent.SimpleTask;

public class VoiceCallShare extends PassphraseRequiredActivity {
  
  private static final String TAG = Log.tag(VoiceCallShare.class);
  
  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    super.onCreate(savedInstanceState, ready);

    if (getIntent().getData() != null && "content".equals(getIntent().getData().getScheme())) {
      Cursor cursor = null;
      
      try {
        cursor = getContentResolver().query(getIntent().getData(), null, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
          String destination = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.Data.DATA1));

          SimpleTask.run(() -> Recipient.external(this, destination), recipient -> {
            if (!TextUtils.isEmpty(destination)) {
              ApplicationDependencies.getSignalCallManager().startOutgoingAudioCall(recipient);

              Intent activityIntent = new Intent(this, WebRtcCallActivity.class);
              activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(activityIntent);
            }
          });
        }
      } finally {
        if (cursor != null) cursor.close();
      }
    }
    
    finish();
  }
}
