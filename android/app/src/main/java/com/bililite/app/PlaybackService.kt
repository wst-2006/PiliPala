package com.bililite.app

import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.bililite.plugin.PlayerBridge
import com.bililite.ui.PlayReq

/** Owns playback across window changes; the activity only owns the video surface. */
class PlaybackService : MediaSessionService() {
    companion object {
        const val LOCAL_BIND = "com.bililite.app.BIND_PLAYBACK"
    }

    inner class LocalBinder : Binder() {
        val service: PlaybackService get() = this@PlaybackService
    }

    lateinit var player: ExoPlayer
        private set
    private var session: MediaSession? = null
    var request by mutableStateOf<PlayReq?>(null)
        private set
    val cidState = mutableStateOf(0L)
    val fullscreenState = mutableStateOf(false)
    private val stoppedPositions = mutableMapOf<Pair<String, Long>, Long>()
    var sourceKey: Triple<String, Long, Int>? = null
    var sourceUrl = ""
    var qualities: List<Pair<Int, String>> = emptyList()
    val initialPositionState = mutableStateOf(false)
    var initialPositionApplied by initialPositionState
    val pendingSeekState = mutableStateOf<Long?>(null)

    override fun onCreate() {
        super.onCreate()
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(20000, 60000, 1500, 3000)
            .build()
        player = ExoPlayer.Builder(this).setLoadControl(loadControl).build().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), true)
            setHandleAudioBecomingNoisy(true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            trackSelectionParameters = trackSelectionParameters.buildUpon()
                .setPreferredTextLanguage("zh")
                .setPreferredTextRoleFlags(C.ROLE_FLAG_SUBTITLE).build()
        }
        val activityIntent = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        session = MediaSession.Builder(this, player).setSessionActivity(activityIntent).build()
        addSession(session!!)
    }

    override fun onBind(intent: Intent?): IBinder? =
        if (intent?.action == LOCAL_BIND) LocalBinder() else super.onBind(intent)

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        if (controllerInfo.isTrusted || controllerInfo.packageName == packageName) session else null

    fun open(next: PlayReq, cid: Long = next.video.cid) {
        startService(Intent(this, PlaybackService::class.java))
        rememberPosition()
        player.stop()
        player.clearMediaItems()
        sourceKey = null
        sourceUrl = ""
        qualities = emptyList()
        initialPositionApplied = false
        pendingSeekState.value = null
        cidState.value = cid
        request = next
    }

    fun stopPlayback() {
        rememberPosition()
        player.stop()
        player.clearMediaItems()
        sourceKey = null
        sourceUrl = ""
        request = null
        fullscreenState.value = false
        PlayerBridge.detach()
        stopSelf()
    }

    fun rememberPosition() {
        sourceKey?.let { stoppedPositions[it.first to it.second] = player.currentPosition.coerceAtLeast(0) }
    }

    fun positionFor(bvid: String, cid: Long): Long =
        if (sourceKey?.let { it.first == bvid && it.second == cid } == true)
            player.currentPosition.coerceAtLeast(0)
        else stoppedPositions[bvid to cid] ?: 0L

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopPlayback()
    }

    override fun onDestroy() {
        PlayerBridge.detach()
        session?.release()
        session = null
        player.release()
        super.onDestroy()
    }
}
