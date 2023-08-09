package com.example.yemeksiparisuygulamasikotlin.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.yemeksiparisuygulamasikotlin.data.entity.CRUDCevap
import com.example.yemeksiparisuygulamasikotlin.data.entity.SepetYemekler
import com.example.yemeksiparisuygulamasikotlin.data.entity.SepetYemeklerCevap
import com.example.yemeksiparisuygulamasikotlin.retrofit.ApiUtils
import com.example.yemeksiparisuygulamasikotlin.retrofit.SepetYemeklerDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SepetYemeklerDaoRepository {
    var sydao : SepetYemeklerDao
    var sepetYemekListesi = MutableLiveData<List<SepetYemekler>>()

    init {

        sydao = ApiUtils.getSepetYemeklerDao()
        sepetYemekListesi = MutableLiveData()
    }


    fun sepeteEkle(yemek_adi:String,yemek_resim_adi:String,yemek_fiyat:Int,yemek_siparis_adet:Int,kullanici_adi:String){
        var control = false
        sepetYemekYukle(kullanici_adi)



        if(sepetYemekListesi.value?.isEmpty() == false){
            for(i in sepetYemekListesi.value.orEmpty()){
                if(i.yemek_adi.contains(yemek_adi)){
                    control = true
                    var adet = i.yemek_siparis_adet + yemek_siparis_adet
                    sepetYemekSil(i.sepet_yemek_id,i.kullanici_adi)
                    sydao.sepeteYemekEkle(yemek_adi,yemek_resim_adi,yemek_fiyat,adet,kullanici_adi)
                        .enqueue(object : Callback<CRUDCevap> {
                            override fun onResponse(call: Call<CRUDCevap>, response: Response<CRUDCevap>) {
                                sepetYemekYukle(kullanici_adi)
                            }
                            override fun onFailure(call: Call<CRUDCevap>, t: Throwable) {}
                        })
                    break
                }
            }

            if(control == false){
                sydao.sepeteYemekEkle(yemek_adi,yemek_resim_adi,yemek_fiyat,yemek_siparis_adet,kullanici_adi)
                    .enqueue(object : Callback<CRUDCevap> {
                        override fun onResponse(call: Call<CRUDCevap>, response: Response<CRUDCevap>) {
                            sepetYemekYukle(kullanici_adi)

                        }
                        override fun onFailure(call: Call<CRUDCevap>, t: Throwable) {}
                    })
            }

        }
        else{

            sydao.sepeteYemekEkle(yemek_adi,yemek_resim_adi,yemek_fiyat,yemek_siparis_adet,kullanici_adi)
                .enqueue(object : Callback<CRUDCevap> {
                    override fun onResponse(call: Call<CRUDCevap>, response: Response<CRUDCevap>) {
                        sepetYemekYukle(kullanici_adi)

                    }
                    override fun onFailure(call: Call<CRUDCevap>, t: Throwable) {}
                })
            sepetYemekYukle(kullanici_adi)

        }



    }


    fun sepetYemekYukle(kullanici_adi: String){
        sydao.sepetYemekGetir(kullanici_adi).enqueue(object : Callback<SepetYemeklerCevap>{
            override fun onResponse(call: Call<SepetYemeklerCevap>, response: Response<SepetYemeklerCevap>) {
                val liste = response.body()!!.sepet_yemekler
                sepetYemekListesi.value = liste

            }

            override fun onFailure(call: Call<SepetYemeklerCevap>, t: Throwable) {
                sepetYemekListesi.value = ArrayList<SepetYemekler>()
            }

        })
    }

    fun sepetYemekGetir() : MutableLiveData<List<SepetYemekler>>{
        return sepetYemekListesi
    }

    fun sepetYemekSil(sepet_yemek_id:Int,kullanici_adi: String){

        sydao.sepetYemekSil(sepet_yemek_id,kullanici_adi).enqueue(object : Callback<CRUDCevap>{
            override fun onResponse(call: Call<CRUDCevap>, response: Response<CRUDCevap>) {
                sepetYemekYukle(kullanici_adi)
            }

            override fun onFailure(call: Call<CRUDCevap>, t: Throwable) {}
        })

    }

}