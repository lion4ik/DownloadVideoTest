package com.lion4ik.github.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
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
        editUrl.setText("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        editUrl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(newText: CharSequence, p1: Int, p2: Int, p3: Int) {
                mainViewModel.onVideoUrlChanged(newText.toString())
            }

        })
        btnPlay.setOnClickListener {
            mainViewModel.onPlayPauseClicked(editUrl.text.toString())
        }
        btnDownload.setOnClickListener {
            downloadFileWithPermissionCheck(editUrl.text.toString())
        }
        videoView.setOnErrorListener { mp, what, extra ->
            Toast.makeText(
                context,
                "An error occurred while tried to play video",
                Toast.LENGTH_SHORT
            ).show()
            videoView.suspend()
            true
        }
        videoView.setOnPreparedListener {
            videoView.start()
        }
        btnUrl1.setOnClickListener {
            editUrl.setText("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
        }
        btnUrl2.setOnClickListener {
            editUrl.setText("https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4")
        }
        btnUrl3.setOnClickListener {
            editUrl.setText("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4")
        }
    }

    private fun observeData() {
        mainViewModel.videoUri.nonNullObserve(viewLifecycleOwner) {
            if (videoView.isPlaying) {
                videoView.pause()
            } else {
                if (videoView.uri != it) {
//                    videoView.stopPlayback()
                    videoView.suspend()
                    videoView.setVideoURI(it)
                } else {
                    videoView.start()
                }
            }
        }
        mainViewModel.videoUrl.nonNullObserve(viewLifecycleOwner) {
            stopPlayer()
        }
    }

    private fun stopPlayer() {
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }
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
