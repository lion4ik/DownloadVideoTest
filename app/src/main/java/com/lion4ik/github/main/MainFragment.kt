package com.lion4ik.github.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.lion4ik.github.R
import com.lion4ik.github.util.nonNullObserve
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        private const val URL1 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        private const val URL2 =
            "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"
        private const val URL3 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
    }

    private val mainViewModel: MainViewModel by viewModel()

    override fun onStop() {
        super.onStop()
        videoView.stopPlayback()
    }

    override fun onPause() {
        super.onPause()
        // In Android versions less than N (7.0, API 24), onPause() is the
        // end of the visual lifecycle of the app.  Pausing the video here
        // prevents the sound from continuing to play even after the app
        // disappears.
        //
        // This is not a problem for more recent versions of Android because
        // onStop() is now the end of the visual lifecycle, and that is where
        // most of the app teardown should take place.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initViews()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        editUrl.setText(URL1)
        editUrl.addTextChangedListener(onTextChanged = { newText, _, _, _ ->
            mainViewModel.onVideoUrlChanged(newText.toString())
        })
        btnPlay.setOnClickListener {
            mainViewModel.onPlayPauseClicked(editUrl.text.toString())
        }
        btnDownload.setOnClickListener {
            downloadFileWithPermissionCheck(editUrl.text.toString())
        }
        videoView.setOnErrorListener { mp, what, extra ->
            showToast(R.string.error_play_video)
            videoView.suspend()
            true
        }
        videoView.setOnPreparedListener {
            videoView.start()
        }
        btnUrl1.setOnClickListener {
            editUrl.setText(URL1)
        }
        btnUrl2.setOnClickListener {
            editUrl.setText(URL2)
        }
        btnUrl3.setOnClickListener {
            editUrl.setText(URL3)
        }
    }

    private fun observeData() {
        mainViewModel.videoUri.nonNullObserve(viewLifecycleOwner) {
            when {
                videoView.isPlaying -> videoView.pause()
                videoView.uri != it -> {
                    videoView.suspend()
                    videoView.setVideoURI(it)
                }
                else -> videoView.start()
            }
        }
        mainViewModel.videoUrl.nonNullObserve(viewLifecycleOwner) {
            stopPlayer()
        }
        mainViewModel.downloadResult.nonNullObserve(viewLifecycleOwner) {
            if (it.status != DownloadManager.STATUS_SUCCESSFUL) {
                showToast(R.string.error_download_video)
            }
        }
        mainViewModel.error.nonNullObserve(viewLifecycleOwner) {
            showToast(it)
        }
    }

    private fun stopPlayer() {
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }
    }

    private fun showToast(@StringRes msgResId: Int) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun downloadFile(url: String) {
        mainViewModel.onDownloadClicked(url)
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationalWriteExternalStorage(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean =
        Manifest.permission.WRITE_EXTERNAL_STORAGE == permission

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        onRequestPermissionsResult(requestCode, grantResults)
    }

}
