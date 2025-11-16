package br.com.noartcode.theprice.platform.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.noartcode.theprice.R
import androidx.core.net.toUri


class AndroidNotifier (
    private val context: Context
) : INotifier {

    private val notificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    @SuppressLint("MissingPermission")
    override fun showOverduePaymentNotification(title: String, description: String, paymentId:String) {
        val contentIntent = pendingIntentToOpenPayment(paymentId)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_stat_pric_logo)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        createNotificationChannel()

        if (areNotificationEnabled) {
            NotificationManagerCompat
                .from(context)
                .notify(paymentId.hashCode(), builder.build())
        }
    }


    private val areNotificationEnabled
        get() = NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }.also { notificationManager.createNotificationChannel(it) }
        }
    }

    private fun pendingIntentToOpenPayment(paymentId: String) : PendingIntent {
        val uri = "theprice://payment/$paymentId".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP // <-- performs a bitwise OR to combine multiple flags
        }

        val requestCode = paymentId.hashCode()
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // <-- performs a bitwise OR to combine multiple flags
        )

    }

    companion object {
        private const val CHANNEL_DESCRIPTION = "Notification channel description"
        private const val CHANNEL_NAME = "Notification channel name"
        private const val CHANNEL_ID = "Notification channel id"
        private const val NOTIFICATION_ID = 1
    }
}