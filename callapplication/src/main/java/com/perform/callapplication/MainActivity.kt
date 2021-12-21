package com.perform.callapplication

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            PermissionX.init(this)
                .permissions(Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CALL_LOG,Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason { scope, deniedList ->
                    //先获取正常的权限，获取完后，走这里获取特殊权限（如悬浮窗等，必须要去系统页面手动设置）
                    val message = "需要您开启以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "去开启", "拒绝")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        initEvent()
                    } else {
                        Toast.makeText(this,"您拒绝了如下权限：$deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            //6.0之前不需要动态申请权限
            initEvent()
        }
    }

    private fun initEvent(){
        //判断服务是否已启动
        if (!MyService.live){
            //8.0前后启动前台服务的方法不同
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this,MyService::class.java))
            } else {
                startService(Intent(this,MyService::class.java))
            }
        }
    }
}