package br.com.noartcode.theprice.platform.notification

interface INotifier {
    fun showOverduePaymentNotification(title: String, description: String, paymentId:String)
}