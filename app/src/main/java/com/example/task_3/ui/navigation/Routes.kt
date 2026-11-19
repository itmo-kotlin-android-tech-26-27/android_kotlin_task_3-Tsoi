package com.example.task_3.ui.navigation

import android.net.Uri

object Routes {
    const val SECTIONS = "sections"
    const val SECTION_ID = "sectionId"
    const val GROUP_ID = "groupId"
    const val SECTION = "section/{sectionId}"
    const val GROUP = "section/{sectionId}/group/{groupId}"

    fun section(sectionId: String) = "section/${Uri.encode(sectionId)}"

    fun group(sectionId: String, groupId: String) =
        "section/${Uri.encode(sectionId)}/group/${Uri.encode(groupId)}"
}
