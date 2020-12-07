package com.wwxd.translation

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.wwxd.base.BaseActivity
import com.wwxd.utils.*
import com.wwxd.utils.http.Api
import com.wwxd.utils.http.IHttpResponse
import com.wwxd.utils.http.OkHttp
import kotlinx.android.synthetic.main.activity_trans_def.*
import java.lang.StringBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

/**
 * user：LuHao
 * time：2020/12/3 17:55
 * describe：翻译
 */
class TransDefActivity : BaseActivity() {
    private val lauageNameFrom = StringUtil.getStrings(R.array.lauage_name_from_list)
    private val lauageCodeFrom = StringUtil.getStrings(R.array.lauage_code_from_list)
    private val lauageNameTo = StringUtil.getStrings(R.array.lauage_name_to_list)
    private val lauageCodeTo = StringUtil.getStrings(R.array.lauage_code_to_list)
    private var lauageFromType = lauageCodeFrom[0]
    private var lauageToType = lauageCodeTo[0]

    override fun getContentView(): Int {
        return R.layout.activity_trans_def
    }

    override fun init() {
        spinnerCountriesFrom.setAdapter(OnSpinnerAdapterFrom())
        spinnerCountriesFrom.setOnItemSelectedListener(OnItemSelectedListenerFrom())
        spinnerCountriesTo.setAdapter(OnSpinnerAdapterTo())
        spinnerCountriesTo.setOnItemSelectedListener(OnItemSelectedListenerTo())
        btnTranslation.setOnClickListener {
            if (!TextUtils.isEmpty(etContent.text)) {
                etTranslation.setText("")
                val content = etContent.text.toString()
                getSign(content)
                Api.Translation.iHttpResponse = object : IHttpResponse {
                    override fun onSuccess(json: String) {
                        val jsonArray = GsonUtil.getJsonArray(json, "trans_result")
                        if (jsonArray != null && jsonArray.size() > 0) {
                            addDefUseNum()
                            val contentStr1 = StringBuffer()
                            val contentStr2 = StringBuffer()
                            contentStr1.append(getString(R.string.str_translation_from))
                            jsonArray.forEach { jsonElement ->
                                val jsonObject = jsonElement.asJsonObject
                                contentStr1.append(jsonObject.get("src").asString)
                                    .append("\n")
                                contentStr2.append(jsonObject.get("dst").asString)
                                    .append("\n")
                            }
                            contentStr1
                                .append(getString(R.string.str_translation_to))
                                .append("\n")
                                .append(contentStr2)
                            etTranslation.setText(contentStr1)
                        } else {
                            etTranslation.setText(getString(R.string.str_translation_fail))
                        }
                    }

                    override fun onFailure(error: String) {
                        etTranslation.setText(error)
                    }
                }
                OkHttp.requestPost(Api.Translation)
            }
        }
    }

    //生成请求参数
    private fun getSign(content: String) {
        if (content.length >= 2000) {
            ToastUtil.showFailureToast(getString(R.string.str_max_trans))
        } else {
            Api.Translation.params["q"] = content//翻译内容
            Api.Translation.params["from"] = lauageFromType//翻译源语言
            Api.Translation.params["to"] = lauageToType//翻译目标语言
            Api.Translation.params["appid"] = BuildConfig.app_id//APP ID
            val random = Random.nextLong(1000000000L, 9999999999L)
            Api.Translation.params["salt"] = random //随机数
            val signStr = StringBuilder()
            signStr.append(Api.Translation.params["appid"])
            signStr.append(Api.Translation.params["q"])
            signStr.append(Api.Translation.params["salt"])
            signStr.append(BuildConfig.app_secret)
            Api.Translation.params["sign"] = MD5Util.md5(signStr.toString()) //随机数
        }
    }

    //选择语言
    private inner class OnSpinnerAdapterFrom :
        ArrayAdapter<String>(this, R.layout.item_lauage_spinner, lauageNameFrom) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(lauageNameFrom[position])
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(lauageNameFrom[position])
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerFrom : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (lauageToType.equals(lauageCodeFrom[position])) {
                spinnerCountriesFrom.setSelection(lauageCodeFrom.indexOf(lauageFromType), true)
                ToastUtil.showLongToast(getString(R.string.str_translation_error))
            } else
                lauageFromType = lauageCodeFrom[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    //选择语言
    private inner class OnSpinnerAdapterTo :
        ArrayAdapter<String>(this, R.layout.item_lauage_spinner, lauageNameTo) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(lauageNameTo[position])
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(lauageNameTo[position])
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerTo : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (lauageFromType.equals(lauageCodeTo[position])) {
                ToastUtil.showLongToast(getString(R.string.str_translation_error))
                spinnerCountriesTo.setSelection(lauageCodeTo.indexOf(lauageToType), true)
            } else
                lauageToType = lauageCodeTo[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun addDefUseNum() {
        SharedPreferencesUtil.saveInt(
            TransContanst.TransDefNum,
            SharedPreferencesUtil.getInt(TransContanst.TransDefNum, 0) + 1
        )
    }
}