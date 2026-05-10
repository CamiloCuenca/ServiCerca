package com.servicerca.app.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.servicerca.app.domain.model.Comment
import com.servicerca.app.domain.repository.CommentRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    override val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val commentsCollection = firestore.collection("comments")

    init {
        observeAllComments()
    }

    private fun observeAllComments() {
        commentsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("CommentRepository", "Error observando comentarios", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _comments.value = snapshot.documents.mapNotNull { it.toComment() }
            }
        }
    }

    override suspend fun save(comment: Comment) {
        try {
            commentsCollection.document(comment.id).set(comment.toFirestoreMap()).await()
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error guardando comentario", e)
            throw e
        }
    }

    override suspend fun update(comment: Comment) {
        try {
            commentsCollection.document(comment.id).set(comment.toFirestoreMap()).await()
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error actualizando comentario", e)
            throw e
        }
    }

    override suspend fun delete(id: String) {
        try {
            commentsCollection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error eliminando comentario", e)
            throw e
        }
    }

    override suspend fun findById(id: String): Comment? {
        return try {
            val doc = commentsCollection.document(id).get().await()
            doc.toComment()
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error buscando comentario por id", e)
            null
        }
    }

    override fun findByUserId(userId: String): StateFlow<List<Comment>> {
        return _comments.map { list -> list.filter { it.userId == userId } }
            .stateIn(GlobalScope, SharingStarted.WhileSubscribed(5000),
                _comments.value.filter { it.userId == userId })
    }

    override fun findByServiceId(serviceId: String): StateFlow<List<Comment>> {
        return _comments.map { list -> list.filter { it.serviceId == serviceId } }
            .stateIn(GlobalScope, SharingStarted.WhileSubscribed(5000),
                _comments.value.filter { it.serviceId == serviceId })
    }

    override fun findByRating(rating: Int): StateFlow<List<Comment>> {
        return _comments.map { list -> list.filter { it.rating == rating } }
            .stateIn(GlobalScope, SharingStarted.WhileSubscribed(5000),
                _comments.value.filter { it.rating == rating })
    }

    private fun Comment.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "serviceId" to serviceId,
        "userName" to userName,
        "userAvatar" to userAvatar,
        "rating" to rating,
        "text" to text,
        "date" to date,
        "timeAgo" to timeAgo
    )

    private fun DocumentSnapshot.toComment(): Comment? {
        return try {
            Comment(
                id = getString("id") ?: this.id,
                userId = getString("userId") ?: "",
                serviceId = getString("serviceId") ?: "",
                userName = getString("userName") ?: "",
                userAvatar = getString("userAvatar") ?: "",
                rating = getLong("rating")?.toInt() ?: 0,
                text = getString("text") ?: "",
                date = getLong("date") ?: 0L,
                timeAgo = getString("timeAgo") ?: ""
            )
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error parseando comentario ${this.id}", e)
            null
        }
    }
}
