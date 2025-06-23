package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.Payment
// THIS IS WHY I DON'T LIKE CLEAN ARCHITECTURE, A WHOLE FILE, JUST TO PUT THIS:
fun interface IGetPaymentByID : suspend (String) -> Payment?
