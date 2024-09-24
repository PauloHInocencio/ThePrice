package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.Payment
// THIS IS WHY I DON'T LIKE CLEAN ARCHITECTURE, A WHOLE FILE, JUST TO PUT THIS:
fun interface IGetPaymentByID : suspend (Long) -> Payment?
