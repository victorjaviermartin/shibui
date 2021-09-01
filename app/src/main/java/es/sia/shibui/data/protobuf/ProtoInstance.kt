import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import es.sia.shiba.UserStore
import es.sia.shibui.data.protobuf.UserStoreSerializer

private val USER_DATA_STORE_FILE_NAME = "user_store.pb"

val Context.userDataStore: DataStore<UserStore> by dataStore(
    fileName = USER_DATA_STORE_FILE_NAME,
    serializer = UserStoreSerializer
)