package com.example.task_3.data

import android.content.Context
import com.example.task_3.BuildConfig
import com.example.task_3.domain.SportsGroup
import com.example.task_3.domain.SportsRepository
import com.example.task_3.domain.SportsSection
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class AssetSportsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SportsRepository {
    override suspend fun getSections(): List<SportsSection> = withContext(Dispatchers.IO) {
        val sections = context.assets.open(BuildConfig.DEMO_DATA_FILE).bufferedReader().use {
            JSONArray(it.readText())
        }
        val result = List(sections.length()) { index ->
            parseSection(sections.getJSONObject(index))
        }
        require(result.map { it.id }.distinct().size == result.size) {
            "Section ids must be unique"
        }
        result
    }

    private fun parseSection(section: JSONObject): SportsSection {
        val groups = section.getJSONArray("groups")
        return SportsSection(
            id = section.getString("id"),
            name = section.getString("name"),
            location = section.getString("location"),
            groups = List(groups.length()) { index ->
                parseGroup(groups.getJSONObject(index))
            },
        )
    }

    private fun parseGroup(group: JSONObject): SportsGroup = SportsGroup(
        id = group.getString("id"),
        name = group.getString("name"),
        athleteCount = group.getInt("athleteCount"),
        trainingCount = group.getInt("trainingCount"),
        attendedVisits = group.getInt("attendedVisits"),
    )
}
