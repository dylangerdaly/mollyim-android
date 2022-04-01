package org.thoughtcrime.securesms.stories.viewer

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.thoughtcrime.securesms.database.SignalDatabase
import org.thoughtcrime.securesms.database.model.DistributionListId
import org.thoughtcrime.securesms.database.model.StoryResult
import org.thoughtcrime.securesms.recipients.Recipient
import org.thoughtcrime.securesms.recipients.RecipientId

/**
 * Open for testing
 */
open class StoryViewerRepository {
  fun getStories(): Single<List<RecipientId>> {
    return Single.fromCallable {
      val storyResults: List<StoryResult> = SignalDatabase.mms.orderedStoryRecipientsAndIds.distinctBy { it.recipientId }
      val resolved = storyResults.map { Recipient.resolved(it.recipientId) }

      val doNotCollapse: List<RecipientId> = resolved
        .filterNot { it.isDistributionList || it.shouldHideStory() }
        .map { it.id }

      val myStory: RecipientId = SignalDatabase.recipients.getOrInsertFromDistributionListId(DistributionListId.MY_STORY)

      val myStoriesCount = SignalDatabase.mms.getAllOutgoingStories(true).use {
        var count = 0
        while (it.next != null) {
          if (!it.current.recipient.isGroup) {
            count++
          }
        }

        count
      }

      if (myStoriesCount > 0) {
        listOf(myStory) + doNotCollapse
      } else {
        doNotCollapse
      }
    }.subscribeOn(Schedulers.io())
  }
}
