package com.progressp.config

import com.progressp.util.CurrencyCodeNotFound

enum class CurrencyCode(val code: String) {
    AED("AED"), AFN("AFN"), ALL("ALL"), AMD("AMD"), ANG("ANG"), AOA("AOA"),
    ARS("ARS"), AUD("AUD"), AWG("AWG"), AZN("AZN"), BAM("BAM"), BBD("BBD"),
    BDT("BDT"), BGN("BGN"), BHD("BHD"), BIF("BIF"), BMD("BMD"), BND("BND"),
    BOB("BOB"), BRL("BRL"), BSD("BSD"), BTN("BTN"), BWP("BWP"), BYN("BYN"),
    BZD("BZD"), CAD("CAD"), CDF("CDF"), CHF("CHF"), CLP("CLP"), CNY("CNY"),
    COP("COP"), CRC("CRC"), CUP("CUP"), CVE("CVE"), CZK("CZK"), DJF("DJF"),
    DKK("DKK"), DOP("DOP"), DZD("DZD"), EGP("EGP"), ERN("ERN"), ETB("ETB"),
    EUR("EUR"), FJD("FJD"), FKP("FKP"), GBP("GBP"), GEL("GEL"), GHS("GHS"),
    GIP("GIP"), GMD("GMD"), GNF("GNF"), GTQ("GTQ"), GYD("GYD"), HKD("HKD"),
    HNL("HNL"), HRK("HRK"), HTG("HTG"), HUF("HUF"), IDR("IDR"), ILS("ILS"),
    INR("INR"), IQD("IQD"), IRR("IRR"), ISK("ISK"), JMD("JMD"), JOD("JOD"),
    JPY("JPY"), KES("KES"), KGS("KGS"), KHR("KHR"), KMF("KMF"), KPW("KPW"),
    KWD("KWD"), KRW("KRW"), KYD("KYD"), KZT("KZT"), LAK("LAK"), LBP("LBP"),
    LKR("LKR"), LRD("LRD"), LSL("LSL"), LYD("LYD"), MAD("MAD"), MDL("MDL"),
    MGA("MGA"), MKD("MKD"), MMK("MMK"), MNT("MNT"), MOP("MOP"), MRO("MRO"),
    MUR("MUR"), MVR("MVR"), MWK("MWK"), MXN("MXN"), MYR("MYR"), MZN("MZN"),
    NAD("NAD"), NGN("NGN"), NIO("NIO"), NOK("NOK"), NPR("NPR"), NZD("NZD"),
    OMR("OMR"), PEN("PEN"), PGK("PGK"), PHP("PHP"), PKR("PKR"), PLN("PLN"),
    PYG("PYG"), QAR("QAR"), RON("RON"), RSD("RSD"), RUB("RUB"), RWF("RWF"),
    SAR("SAR"), SBD("SBD"), SCR("SCR"), SDG("SDG"), SEK("SEK"), SGD("SGD"),
    SHP("SHP"), SLL("SLL"), SOS("SOS"), SRD("SRD"), SSP("SSP"), STD("STD"),
    SVC("SVC"), SYP("SYP"), SZL("SZL"), THB("THB"), TJS("TJS"), TMT("TMT"),
    TND("TND"), TOP("TOP"), TRY("TRY"), TTD("TTD"), TWD("TWD"), TZS("TZS"),
    UAH("UAH"), UGX("UGX"), UYU("UYU"), USD("USD"), UZS("UZS"), XAF("XAF"),
    VEF("VEF"), VND("VND"), VUV("VUV"), WST("WST"), XCD("XCD"), XOF("XOF"),
    XPF("XPF"), YER("YER"), ZAR("ZAR"), ZMW("ZMW"), ZWL("ZWL")
}

data class Currency(
    val name: String,
    val symbol: String,
    val emoji: String,
    val code: CurrencyCode
)

object Currencies {
    private val currencies: List<Currency> = listOf(
        Currency("Australian Dollar", "$", "🇦🇺", CurrencyCode.AUD),
        Currency("British Pound", "£", "🇬🇧", CurrencyCode.GBP),
        Currency("Bulgarian Lev", "лв", "🇧🇬", CurrencyCode.BGN),
        Currency("Canadian Dollar", "$", "🇨🇦", CurrencyCode.CAD),
        Currency("Croation Kuna", "kn", "🇭🇷", CurrencyCode.HRK),
        Currency("Czech Koruna", "Kč", "🇨🇿", CurrencyCode.CZK),
        Currency("Danish Krone", "kr", "🇩🇰", CurrencyCode.DKK),
        Currency("Euro", "€", "🇪🇺", CurrencyCode.EUR),
        Currency("Hong Kong Dollar", "$", "🇭🇰", CurrencyCode.HKD),
        Currency("Hungarian Forint", "Ft", "🇭🇺", CurrencyCode.HUF),
        Currency("Icelandic Krona", "kr", "🇮🇸", CurrencyCode.ISK),
        Currency("Indian Rupee", "₹", "🇮🇳", CurrencyCode.INR),
        Currency("Israeli New Shekel", "₪", "🇮🇱", CurrencyCode.ILS),
        Currency("Japanese Yen", "¥", "🇯🇵", CurrencyCode.JPY),
        Currency("Mexican Peso", "$", "🇲🇽", CurrencyCode.MXN),
        Currency("Morocon Dirham", "MAD", "🇲🇦", CurrencyCode.MAD),
        Currency("New Zealand Dollar", "$", "🇳🇿", CurrencyCode.NZD),
        Currency("Norwegian Krone", "kr", "🇳🇴", CurrencyCode.NOK),
        Currency("Polish Zloty", "zł", "🇵🇱", CurrencyCode.PLN),
        Currency("Romanian Leu", "lei", "🇷🇴", CurrencyCode.RON),
        Currency("Saudi Arabian Riyal", "﷼", "🇸🇦", CurrencyCode.SAR),
        Currency("Serbian Dinar", "Дин.", "🇷🇸", CurrencyCode.RSD),
        Currency("Singapore Dollar", "$", "🇸🇬", CurrencyCode.SGD),
        Currency("South African Rand", "R", "🇿🇦", CurrencyCode.ZAR),
        Currency("Swedish Krona", "kr", "🇸🇪", CurrencyCode.SEK),
        Currency("Swiss Franc", "Fr", "🇨🇭", CurrencyCode.CHF),
        Currency("Thai Baht", "฿", "🇹🇭", CurrencyCode.THB),
        Currency("Turkish Lira", "₺", "🇹🇷", CurrencyCode.TRY),
        Currency("US Dollar", "$", "🇺🇸", CurrencyCode.USD),
        Currency("UAE Dirham", "د.إ", "🇦🇪", CurrencyCode.AED),
    )

    private fun isCodeSupported(paramCode: String): Boolean {
        return CurrencyCode.values().any { it.code == paramCode }
    }

    fun getCurrencyByCode(paramCode: String): Currency {
        if(!isCodeSupported(paramCode)) throw CurrencyCodeNotFound(paramCode)
        return currencies.find { it.code == CurrencyCode.valueOf(paramCode) }!!
    }

    fun getAlphabeticalCurrencies(): List<Currency> {
        return currencies.sortedBy { it.name }
    }
}