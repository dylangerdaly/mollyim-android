package org.thoughtcrime.securesms.stories.viewer

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.thoughtcrime.securesms.recipients.RecipientId

class StoryViewerViewModelTest {
  private val testScheduler = TestScheduler()
  private val repository: StoryViewerRepository = mock()

  @Before
  fun setUp() {
    RxJavaPlugins.setInitComputationSchedulerHandler { testScheduler }
    RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
  }

  @After
  fun tearDown() {
    RxJavaPlugins.reset()
  }

  @Test
  fun `Given five stories, when I initialize with story 2, then I expect to be on the right page`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = RecipientId.from(2L)
    whenever(repository.getStories()).doReturn(Single.just(stories))

    // WHEN
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // THEN
    val expectedStartIndex = testSubject.stateSnapshot.pages.indexOf(startStory)
    val actualStartIndex = testSubject.stateSnapshot.page

    assertEquals(expectedStartIndex, actualStartIndex)
  }

  @Test
  fun `Given five stories and am on 1, when I onGoToNext, then I expect to go to 2`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = RecipientId.from(1L)
    whenever(repository.getStories()).doReturn(Single.just(stories))
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // WHEN
    testSubject.onGoToNext(RecipientId.from(1L))
    testScheduler.triggerActions()

    // THEN
    val expectedIndex = 1
    val actualIndex = testSubject.stateSnapshot.page

    assertEquals(expectedIndex, actualIndex)
  }

  @Test
  fun `Given five stories and am on last, when I onGoToNext, then I expect to go to size`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = stories.last()
    whenever(repository.getStories()).doReturn(Single.just(stories))
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // WHEN
    testSubject.onGoToNext(startStory)
    testScheduler.triggerActions()

    // THEN
    val expectedIndex = stories.size
    val actualIndex = testSubject.stateSnapshot.page

    assertEquals(expectedIndex, actualIndex)
  }

  @Test
  fun `Given five stories and am on last, when I onGoToPrevious, then I expect to go to last - 1`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = stories.last()
    whenever(repository.getStories()).doReturn(Single.just(stories))
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // WHEN
    testSubject.onGoToPrevious(startStory)
    testScheduler.triggerActions()

    // THEN
    val expectedIndex = stories.lastIndex - 1
    val actualIndex = testSubject.stateSnapshot.page

    assertEquals(expectedIndex, actualIndex)
  }

  @Test
  fun `Given five stories and am on first, when I onGoToPrevious, then I expect stay at 0`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = stories.first()
    whenever(repository.getStories()).doReturn(Single.just(stories))
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // WHEN
    testSubject.onGoToPrevious(startStory)
    testScheduler.triggerActions()

    // THEN
    val expectedIndex = 0
    val actualIndex = testSubject.stateSnapshot.page

    assertEquals(expectedIndex, actualIndex)
  }

  @Test
  fun `Given five stories and am on first, when I setSelectedPage, then I expect to go to the page I selected`() {
    // GIVEN
    val stories: List<RecipientId> = (1L..5L).map(RecipientId::from)
    val startStory = stories.first()
    whenever(repository.getStories()).doReturn(Single.just(stories))
    val testSubject = StoryViewerViewModel(startStory, repository)
    testScheduler.triggerActions()

    // WHEN
    testSubject.setSelectedPage(2)
    testScheduler.triggerActions()

    // THEN
    val expectedIndex = 2
    val actualIndex = testSubject.stateSnapshot.page

    assertEquals(expectedIndex, actualIndex)
  }
}
