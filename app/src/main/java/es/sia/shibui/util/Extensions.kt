package es.sia.shibui.util

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson

object EncryptionHelper {
    private const val prefName = "${Context.myApp.packageName}_prefs"
    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun getSharedPrefs(context: Context): SharedPreferences {
        val keyEncryptedScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
        val valueEncryptedScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

        return EncryptedSharedPreferences.create(
            prefName,
            masterKey,
            context,
            keyEncryptedScheme,
            valueEncryptedScheme
        )
    }

    fun getEncryptedFile(file: File, context: Context): EncryptedFile {
        val fileEncryptionScheme = EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        return EncryptedFile.Builder(
            file,
            context,
            masterKey,
            fileEncryptionScheme
        ).build()
    }
}

val Context.myAppPreferences: SharedPreferences
    get() = getSharedPreferences("${this.packageName}_${this.javaClass.simpleName}", MODE_PRIVATE)

/*val sharedPreferences by lazy {

    Context.myApp?.let {
        // create the master key
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        // Create the EncryptedSharedPreferences
        EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            it,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}*/


inline fun <reified T : Any> SharedPreferences.getObject(key: String): T? {
    return Gson().fromJson<T>(getString(key, null), T::class.java)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean? ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float? ?: 0.0f) as T
        Int::class -> getInt(key, defaultValue as? Int? ?: 0) as T
        Long::class -> getLong(key, defaultValue as? Long? ?: 0L) as T
        String::class -> getString(key, defaultValue as? String? ?: "") as T
        else -> {
            if (defaultValue is Set<*>) {
                getStringSet(key, defaultValue as Set<String>) as T
            } else {
                val typeName = T::class.java.simpleName
                throw Error("Unable to get shared preference with value type '$typeName'. Use getObject")
            }
        }
    }
}


@Suppress("UNCHECKED_CAST")
inline operator fun <reified T : Any> SharedPreferences.set(key: String, value: T) {
    with(edit()) {
        when (T::class) {
            Boolean::class -> putBoolean(key, value as Boolean)
            Float::class -> putFloat(key, value as Float)
            Int::class -> putInt(key, value as Int)
            Long::class -> putLong(key, value as Long)
            String::class -> putString(key, value as String)
            else -> {
                if (value is Set<*>) {
                    putStringSet(key, value as Set<String>)
                } else {
                    val json = Gson().toJson(value)
                    putString(key, json)
                }
            }
        }
        commit()
    }
}

/**
        Hide Keyboard
 */
fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
}