package com.example.bookasignment.model.request

class TranslateDataResponse(
    var data : Data?
)

data class Data(
    val translations: List<Translations?>?
)

data class Translations(

    var translatedText : String? = null

)