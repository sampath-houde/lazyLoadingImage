import com.example.prashantadvaitfoundationtask.data.MainModelItem
import retrofit2.Response
import retrofit2.http.GET


interface ApiServices {

    @GET("api/v2/content/misc/media-coverages?limit=100")
    suspend fun getData(): Response<MainModelItem>
}