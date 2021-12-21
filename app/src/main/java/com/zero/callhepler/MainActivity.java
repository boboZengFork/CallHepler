package com.zero.callhepler;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zero.callhepler.service.CallService;

import java.util.List;

/**
 * created by Lin on 2017/12/16
 */

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionX.init(MainActivity.this)
                    .permissions(Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CALL_LOG, Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .onExplainRequestReason(new ExplainReasonCallback() {
                        @Override
                        public void onExplainReason(@NonNull @android.support.annotation.NonNull ExplainScope explainScope, @NonNull @android.support.annotation.NonNull List<String> list) {

                            System.out.println("zengbobo ExplainReasonCallback 需要您开启以下权限才能正常使用：");
                        }
                    })
                    .request(new RequestCallback() {

                        @Override
                        public void onResult(boolean b, @NonNull @android.support.annotation.NonNull List<String> list, @NonNull @android.support.annotation.NonNull List<String> list1) {
                            if(b){
                                initEvent();
                            }else{
                                System.out.println("zengbobo RequestCallback 您拒绝了如下权限："+list1);
                            }
                        }
                    });

        } else {
            //6.0之前不需要动态申请权限
            initEvent();
        }

//        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CallWorkActivity.sCall_op = CallWorkActivity.CALL_OP.CALL_ACCEPT;
//            }
//        });
//        findViewById(R.id.button_reject).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CallWorkActivity.sCall_op = CallWorkActivity.CALL_OP.CALL_REJECT;
//            }
//        });
//        findViewById(R.id.button_none).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CallWorkActivity.sCall_op = CallWorkActivity.CALL_OP.NONE;
//            }
//        });
//        startService(new Intent(this, CallService.class));
    }

    private void initEvent(){
        startService(new Intent(this,CallService.class));
        //判断服务是否已启动
//        if (!MyService.live){
//            //8.0前后启动前台服务的方法不同
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(new Intent(this,CallService.class));
//            } else {
//                startService(new Intent(this,CallService.class));
//            }
//        }
    }
}
