package com.forthgreen.app.views.fragments

import android.os.Bundle
import com.forthgreen.app.R
import com.forthgreen.app.viewmodels.BaseViewModel
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.supportFragmentManager
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_play_video.*

/**
 * @author Mohammad Owais
 * @since 04-May-22
 */

class PlayVideoFragment : BaseFragment() {

    companion object {
        const val TAG = "PlayVideoFragment"
        private const val BUNDLE_EXTRAS_VIDEO = "BUNDLE_EXTRAS_VIDEO"
        fun newInstance(videoUri: String): PlayVideoFragment {
            return PlayVideoFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_EXTRAS_VIDEO, videoUri)
                }
            }
        }
    }

    // Variables
    private val video by lazy {
        requireArguments().getString(BUNDLE_EXTRAS_VIDEO)!!
    }

    /*
     * ExoPlayer Variables
     */
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var playbackPosition: Long = 0      // To Track Playback Position
    private var currentWindow = 0           // For Window Position
    private var playWhenReady = true        // To keep the current playing status.
    private var videoUriString: String? = null

    override val layoutId: Int
        get() = R.layout.fragment_play_video

    override val viewModel: BaseViewModel?
        get() = null

    override fun init() {
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        if (video.isNotEmpty()) {
            videoUriString = video

//            val mediaController = MediaController(requireContext())
//            mediaController.setAnchorView(vvPlayer)

            //Setting MediaController and URI, then starting the videoView
//            vvPlayer.setMediaController(mediaController)
            vvPlayer.setVideoPath(videoUriString)
            vvPlayer.requestFocus()
            vvPlayer.start()
        }
    }

    private fun setupListeners() {
        ivCloseVideo.setOnSafeClickListener {
            supportFragmentManager().popBackStack()
        }
    }

    /*
     * ExoPlayer Methods
     */
//    private fun initPlayer() {
//        // Initialize if not already initialized.
//        if (simpleExoPlayer == null) {
//            simpleExoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
//        }
//
//        simpleExoPlayer?.let { simpleExPlayer ->
//            // Bind the player to the view.
//            exoPlayerVideo.player = simpleExoPlayer
//
//            // Assign MediaItem if not null, else return.
//            val mediaItem = videoUriString?.let { uri ->
//                MediaItem.fromUri(Uri.parse(uri))
//            } ?: return
//            simpleExPlayer.apply {
//                setMediaItem(mediaItem)
//                prepare()
//                playWhenReady = this@PlayVideoFragment.playWhenReady
//                seekTo(currentWindow, playbackPosition)
//            }
//        }
//    }
//
//    private fun releasePlayer() {
//        simpleExoPlayer?.let { simpleExPlayer ->
//            playbackPosition = simpleExPlayer.currentPosition
//            currentWindow = simpleExPlayer.currentWindowIndex
//            playWhenReady = simpleExPlayer.playWhenReady
//            simpleExPlayer.release()
//            simpleExoPlayer = null
//        } ?: return
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (Util.SDK_INT >= 24) {
//            initPlayer()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (Util.SDK_INT < 24 || simpleExoPlayer == null) {
//            initPlayer()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (Util.SDK_INT < 24) {
//            releasePlayer()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        if (Util.SDK_INT >= 24) {
//            releasePlayer()
//        }
//    }
}