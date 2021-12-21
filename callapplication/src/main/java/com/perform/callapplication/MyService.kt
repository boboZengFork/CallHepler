package com.perform.callapplication

import android.app.Notification
import android.app.Notification.DEFAULT_SOUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

/**
 *  $
 * @date: 2021/12/21 9:08 上午
 * @author: zengbobo
 */

class MyService : Service() {
    private  val NOTIFICATION_ID=9
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var mPhoneListener: PhoneStateListener
    companion object{
        var TAG  ="MyService"
        //保存服务的开启状态
        var live=false
    }
    //不需要与其他组件交互的话直接返回null即可
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initEvent()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }
    private fun initEvent(){
        telephonyManager= getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //在注册监听的时候就会走一次回调，后面通话状态改变时也会走
        //如下面的代码，在启动服务时如果手机没有通话相关动作，就会直接走一次TelephonyManager.CALL_STATE_IDLE
        mPhoneListener=object :PhoneStateListener(){
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                when(state){
                    //挂断
                    TelephonyManager.CALL_STATE_IDLE->{
                        //toast("挂断${phoneNumber}")
                        Log.i(TAG, "onCallStateChanged: 挂断${phoneNumber}")
                        onCallFinish()
                    }
                    //接听
                    TelephonyManager.CALL_STATE_OFFHOOK->{
                        Toast.makeText(applicationContext,"接听${phoneNumber}",Toast.LENGTH_LONG).show()
                        Log.i(TAG, "onCallStateChanged: 接听${phoneNumber}")
                        startActivity(Intent(applicationContext,MainActivity::class.java).apply {
                            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    //响铃
                    TelephonyManager.CALL_STATE_RINGING->{
                        Toast.makeText(applicationContext,"响铃${phoneNumber}",Toast.LENGTH_LONG).show()
                        Log.i(TAG, "onCallStateChanged: 响铃${phoneNumber}")
                        onCalling(phoneNumber)

                    }
                }
            }
        }
        telephonyManager.listen(mPhoneListener,PhoneStateListener.LISTEN_CALL_STATE)
    }
    //结束通话
    private fun onCallFinish(){

    }
    //被呼叫
    private fun onCalling(phoneNumber:String?){

    }

    override fun onDestroy() {
        super.onDestroy()
        live=false
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        live=true
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 创建服务通知
     */
    private fun createForegroundNotification(): Notification {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 唯一的通知通道的id.
        val notificationChannelId = "notification_channel_id_01"

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            val channelName = "Foreground Service Notification"
            //通道的重要程度
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(notificationChannelId, channelName, importance)
            notificationChannel.description = "Channel description"

            //LED灯
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            //震动
            notificationChannel.vibrationPattern = longArrayOf(0)
            notificationChannel.enableVibration(false)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(this, notificationChannelId)
        //通知标题
        builder.setContentTitle("工作台运行中")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setDefaults(DEFAULT_SOUND)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        //通知内容
        //builder.setContentText("ContentText")
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis())
        //创建通知并返回
        return builder.build()
    }

}