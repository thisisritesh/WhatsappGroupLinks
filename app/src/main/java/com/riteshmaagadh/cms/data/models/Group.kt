package com.riteshmaagadh.cms.data.models

import com.google.firebase.firestore.DocumentId

data class Group(
    @DocumentId
    var documentId: String,
    var title: String,
    var group_link: String,
    var category: String,
    var imageUrl: String,
    var active: Boolean,
    var index: Long
) {
    constructor() : this("", "", "", "", "",false, 0)
}