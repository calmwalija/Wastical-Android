package net.techandgraphics.quantcal.data.local.database.search.tag

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.techandgraphics.quantcal.BaseTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchTagDaoTest : BaseTest() {

  private val tagEntity = SearchTagEntity(query = "lorem", tag = "ipsum")

  override fun populateStaticTestData() = runBlocking {
    super.populateStaticTestData()
    database.searchTagDao.insert(tagEntity)
  }

  @Test
  fun insertThenQueryTag() = runTest {
    val result = database.searchTagDao.query().first()
    assertThat(result.any { it.query == tagEntity.query }, equalTo(true))
  }

  @Test
  fun insertThenUpdateThenQueryTag() = runTest {
    val query = database.searchTagDao.query().first()
    val update = query.map { it.copy(tag = "updated") }
    database.searchTagDao.update(update)
    val result = database.searchTagDao.query().first()
    assertThat(result.any { it.tag == "updated" }, equalTo(true))
  }

  @Test
  fun insertThenUpdateThenQueryThenDeleteTag() = runTest {
    val query = database.searchTagDao.query().first()
    database.searchTagDao.delete(query)
    val result = database.searchTagDao.query().first()
    assertThat(result.isEmpty(), equalTo(true))
  }
}
