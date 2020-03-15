package com.lion4ik.github.ui.main

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.lion4ik.github.R
import com.lion4ik.github.nonNullObserve
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainFragment : Fragment(R.layout.main_fragment) {

    private val mainViewModel: MainViewModel by viewModel()

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val referenceId =
                intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d("DEBUG", "download id = $referenceId")
        }

    }

    override fun onStart() {
        super.onStart()
        context?.registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(downloadReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        val vidAddress =
            "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"
        val vidUri: Uri = Uri.parse(vidAddress)
//        videoView.setVideoURI(vidUri)
//        videoView.start()
        editUrl.setText(vidAddress)
        editUrl.addTextChangedListener( object: TextWatcher{
            override fun afterTextChanged(editable: Editable) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(newText: CharSequence, p1: Int, p2: Int, p3: Int) {
                mainViewModel.onVideoUrlChanged(newText.toString())
            }

        })
        btnPlay.setOnClickListener {
//            if (videoView.isPlaying) {
//                videoView.pause()
//            } else {
//                videoView.start()
//            }
            mainViewModel.onPlayPauseClicked(editUrl.text.toString())
        }
        btnDownload.setOnClickListener {
            downloadFileWithPermissionCheck(vidAddress)
        }
    }

    private fun observeData() {
        mainViewModel.videoUri.nonNullObserve(viewLifecycleOwner) {
            if (videoView.isPlaying) {
                videoView.pause()
            } else {
                if (videoView.bufferPercentage == 0) {
                    videoView.setVideoURI(it)
                }
                videoView.start()
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
