package umc.cozymate.ui.feed

interface FeedClickListener {
    fun deletePost()
    fun editPost()
    fun deleteComment(commentId : Int)

}