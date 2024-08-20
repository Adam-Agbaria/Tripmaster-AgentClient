// CommandService.java
package dev.adamag.travelagentfront.network;

import dev.adamag.travelagentfront.model.BoundaryCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommandService {

    @POST("superapp/miniapp/{miniAppName}")
    Call<BoundaryCommand[]> createCommand(@Path("miniAppName") String miniAppName, @Body BoundaryCommand boundaryCommand);
}
