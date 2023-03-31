//package com.forthgreen.app.views.dialogfragments
//
//import android.net.Uri
//import com.forthgreen.app.R
//import com.google.android.exoplayer2.DefaultLoadControl
//import com.google.android.exoplayer2.DefaultRenderersFactory
//import com.google.android.exoplayer2.ExoPlayer
//import com.google.android.exoplayer2.ExoPlayerFactory
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.ui.PlayerView
//import com.google.android.exoplayer2.upstream.DefaultAllocator
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import kotlinx.android.synthetic.main.dialogfragment_video_play.*
//
///**
// * @author shraychona@gmail.com
// * @since 23 Jul 2019
// */
//class VideoPlayDialogFragment : BaseDialogFragment() {
//
//    companion object {
//        fun newInstance(videoURL: String): VideoPlayDialogFragment {
//            val mVideoPlayDialogFragment = VideoPlayDialogFragment()
//            mVideoPlayDialogFragment.videoURL = videoURL
//            return mVideoPlayDialogFragment
//        }
//    }
//
//    private var videoURL = ""
//    private lateinit var videoView: PlayerView
//    private lateinit var exoPlayer: ExoPlayer
//    private var player: ExoPlayer? = null
//
//    override val isFullScreenDialog: Boolean
//        get() = true
//
//    override val layoutId: Int
//        get() = R.layout.dialogfragment_video_play
//
//    override fun init() {
//        initializePlayer()
//        buildMediaSource(Uri.parse(videoURL))
//    }
//
//    private fun initializePlayer() {
//        if (player == null) {
//            // 1. Create a default TrackSelector
//            val loadControl = DefaultLoadControl(
//                    DefaultAllocator(true, 16),
//                    VideoPlayerConfig.MIN_BUFFER_DURATION,
//                    VideoPlayerConfig.MAX_BUFFER_DURATION,
//                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
//                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true)
//
//            val bandwidthMeter = DefaultBandwidthMeter()
//            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
//            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
//            // 2. Create the player
//            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(requireContext()), trackSelector, loadControl)
//            epVideo.player = player
//        }
//    }
//
//    private fun buildMediaSource(mUri: Uri) {
//        // Measures bandwidth during playback. Can be null if not required.
//        val bandwidthMeter = DefaultBandwidthMeter()
//        // Produces DataSource instances through which media data is loaded.
//        val dataSourceFactory = DefaultDataSourceFactory(requireContext(),
//                Util.getUserAgent(requireContext(), getString(R.string.app_name)), bandwidthMeter)
//        // This is the MediaSource representing the media to be played.
//        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(mUri)
//        // Prepare the player with the source.
//        player?.prepare(videoSource)
//        player?.playWhenReady = true
//    }
//
//    private fun releasePlayer() {
//        if (player != null) {
////            playbackPosition = player?.currentPosition
////            currentWindow = player?.currentWindowIndex
////            playWhenReady = player?.playWhenReady
//            player?.release()
//            player = null
//        }
//    }
//
//    object VideoPlayerConfig {
//        //Minimum Video you want to buffer while Playing
//        const val MIN_BUFFER_DURATION = 5000
//        //Max Video you want to buffer during PlayBack
//        const val MAX_BUFFER_DURATION = 55000
//        //Min Video you want to buffer before start Playing it
//        const val MIN_PLAYBACK_START_BUFFER = 10000
//        //Min video You want to buffer when user resumes video
//        const val MIN_PLAYBACK_RESUME_BUFFER = 5000
//    }
//
//    override fun onDestroyView() {
//        releasePlayer()
//        super.onDestroyView()
//    }
//}
